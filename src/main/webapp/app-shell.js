(function () {
    'use strict';

    window.TS = window.TS || {};

    var initialPathname = window.location.pathname || '/';
    var APP_CONTEXT_PATH = (function () {
        var shellSuffix = '/app-shell.html';
        if (initialPathname === shellSuffix) {
            return '';
        }
        if (initialPathname.slice(-shellSuffix.length) === shellSuffix) {
            return initialPathname.slice(0, -shellSuffix.length);
        }
        return '';
    })();

    var shell = document.getElementById('tsShell');
    var shellBody = document.getElementById('tsShellBody');
    var sidebar = document.getElementById('tsSidebar');
    var sidebarFrame = document.getElementById('tsSidebarFrame');
    var contentFrame = document.getElementById('tsContent');
    var toggleButton = document.getElementById('tsSidebarToggle');
    var sidebarHandle = document.getElementById('tsSidebarHandle');
    var backdrop = document.getElementById('tsSidebarBackdrop');
    var brandLink = document.getElementById('tsShellBrandLink');
    var primaryActionSlot = document.getElementById('tsShellPrimaryAction');
    var createActionSlot = document.getElementById('tsShellCreateAction');
    var searchForm = document.getElementById('tsShellSearchForm');
    var searchHidden = document.getElementById('tsShellSearchHidden');
    var searchInput = document.getElementById('tsShellSearchInput');
    var userButton = document.getElementById('tsShellUserButton');
    var userMenu = document.getElementById('tsShellUserMenu');
    var userAvatar = document.getElementById('tsShellUserAvatar');
    var userName = document.getElementById('tsShellUserName');
    var userRole = document.getElementById('tsShellUserRole');

    var DEFAULT_WIDTH = 296;
    var MIN_WIDTH = 240;
    var MAX_WIDTH = 360;
    var HANDLE_WIDTH = 8;
    var TABLET_BREAKPOINT = 1199;

    var sidebarWidth = parseInt(localStorage.getItem('ts-sidebar-width'), 10) || DEFAULT_WIDTH;
    var sidebarOpen = localStorage.getItem('ts-sidebar') !== 'closed';
    var dragging = false;
    var currentSearchConfig = null;
    var lastUserConfig = null;

    function isTabletOrSmaller() {
        return window.innerWidth <= TABLET_BREAKPOINT;
    }

    function buildAbsoluteAppUrl(path) {
        var base = window.location.origin + (APP_CONTEXT_PATH || '');
        if (!path) {
            return base + '/';
        }
        if (/^https?:\/\//i.test(path) || /^javascript:/i.test(path)) {
            return path;
        }
        if (path.charAt(0) !== '/') {
            path = '/' + path;
        }
        return base + path;
    }

    function normalizeNavigationUrl(url) {
        if (!url || typeof url !== 'string') {
            return '';
        }
        if (/^javascript:/i.test(url)) {
            return url;
        }
        return new URL(url, window.location.origin + (APP_CONTEXT_PATH || '') + '/').toString();
    }

    function isPathInsideAppContext(pathname) {
        if (!pathname || pathname.charAt(0) !== '/') {
            return false;
        }
        if (!APP_CONTEXT_PATH) {
            return true;
        }
        return pathname === APP_CONTEXT_PATH || pathname.indexOf(APP_CONTEXT_PATH + '/') === 0;
    }

    function isValidInternalUrl(url) {
        if (!url || typeof url !== 'string') {
            return false;
        }
        try {
            var parsed = new URL(url, window.location.origin + (APP_CONTEXT_PATH || '') + '/');
            if (parsed.origin !== window.location.origin) {
                return false;
            }
            if (parsed.protocol !== 'http:' && parsed.protocol !== 'https:') {
                return false;
            }
            return isPathInsideAppContext(parsed.pathname);
        } catch (e) {
            return false;
        }
    }

    function textOf(node) {
        if (!node) {
            return '';
        }
        return (node.textContent || node.innerText || '').replace(/\s+/g, ' ').trim();
    }

    function clearChildren(node) {
        while (node && node.firstChild) {
            node.removeChild(node.firstChild);
        }
    }

    function decodeHtmlEntities(value) {
        if (!value || typeof value !== 'string') {
            return '';
        }
        var textarea = document.createElement('textarea');
        textarea.innerHTML = value;
        return textarea.value.replace(/\s+/g, ' ').trim();
    }

    function setSidebarState(nextOpen) {
        sidebarOpen = !!nextOpen;
        localStorage.setItem('ts-sidebar', sidebarOpen ? 'open' : 'closed');
        applyLayout();
    }

    function toggleSidebar() {
        setSidebarState(!sidebarOpen);
    }

    function applyLayout() {
        if (!isTabletOrSmaller()) {
            shellBody.style.gridTemplateColumns = sidebarOpen
                ? sidebarWidth + 'px ' + HANDLE_WIDTH + 'px 1fr'
                : '0px 0px 1fr';
        } else {
            shellBody.style.removeProperty('grid-template-columns');
        }

        shell.classList.toggle('sidebar-collapsed', !sidebarOpen);
        toggleButton.setAttribute('aria-expanded', String(sidebarOpen));

        if (backdrop) {
            if (isTabletOrSmaller() && sidebarOpen) {
                backdrop.style.display = 'block';
                requestAnimationFrame(function () {
                    backdrop.classList.add('visible');
                });
            } else {
                backdrop.classList.remove('visible');
                setTimeout(function () {
                    if (!backdrop.classList.contains('visible')) {
                        backdrop.style.display = 'none';
                    }
                }, 300);
            }
        }
    }

    function closeUserMenu() {
        userMenu.hidden = true;
        userButton.setAttribute('aria-expanded', 'false');
    }

    function closeActionMenus() {
        document.querySelectorAll('.ts-shell-action-menu').forEach(function (menu) {
            var button = menu.querySelector('.ts-shell-action-button');
            var dropdown = menu.querySelector('.ts-shell-action-dropdown');
            menu.querySelectorAll('.ts-shell-action-submenu-toggle[aria-expanded="true"]').forEach(function (toggle) {
                toggle.setAttribute('aria-expanded', 'false');
            });
            menu.querySelectorAll('.ts-shell-action-submenu').forEach(function (submenu) {
                submenu.hidden = true;
            });
            if (dropdown) {
                dropdown.hidden = true;
            }
            if (button) {
                button.setAttribute('aria-expanded', 'false');
            }
        });
    }

    function toggleUserMenu() {
        if (userButton.disabled) {
            return;
        }
        var expanded = userButton.getAttribute('aria-expanded') === 'true';
        if (expanded) {
            closeUserMenu();
        } else {
            userMenu.hidden = false;
            userButton.setAttribute('aria-expanded', 'true');
        }
    }

    function createMenuItem(config) {
        var item = document.createElement('a');
        item.className = 'ts-shell-user-menu-item';
        item.textContent = config.label;
        item.setAttribute('role', 'menuitem');
        item.href = config.href;
        if (config.target) {
            item.target = config.target;
        }
        item.addEventListener('click', function () {
            closeUserMenu();
        });
        return item;
    }

    function applyUserConfig(config) {
        var avatarUtils = window.TSAvatar || {};
        var menuItemCount = 0;
        var displayName = config && config.name ? config.name : 'TrackStudio';
        var displayRole = config && config.role ? config.role : 'Рабочее пространство';

        clearChildren(userMenu);

        if (config && config.profileHref) {
            userMenu.appendChild(createMenuItem({
                label: config.profileLabel || config.name,
                href: config.profileHref,
                target: /^javascript:/i.test(config.profileHref) ? '' : 'mainFrame'
            }));
            menuItemCount += 1;
        }

        if (config && config.actions && config.actions.length) {
            if (menuItemCount > 0) {
                var divider = document.createElement('div');
                divider.className = 'ts-shell-user-menu-divider';
                userMenu.appendChild(divider);
            }
            config.actions.forEach(function (action) {
                userMenu.appendChild(createMenuItem(action));
                menuItemCount += 1;
            });
        }

        userName.textContent = displayName;
        userRole.textContent = displayRole;
        userAvatar.textContent = typeof avatarUtils.getAvatarInitial === 'function'
            ? avatarUtils.getAvatarInitial(displayName)
            : (displayName.charAt(0) || 'T').toUpperCase();
        if (userAvatar.style && typeof avatarUtils.colorFromName === 'function') {
            userAvatar.style.setProperty('--ts-shell-user-avatar-bg', avatarUtils.colorFromName('shell-user:' + displayName));
            userAvatar.style.setProperty('--ts-shell-user-avatar-fg', '#ffffff');
        }

        userButton.disabled = menuItemCount === 0;
        if (userButton.disabled) {
            closeUserMenu();
        }
    }

    function normalizeMenuUrl(url) {
        var decoded = decodeHtmlEntities(url);
        if (!decoded) {
            return '';
        }
        if (/^javascript:/i.test(decoded)) {
            return decoded;
        }
        return normalizeNavigationUrl(decoded);
    }

    function serializeTsMenu(menu) {
        if (!menu || !menu._menuItems || !menu._menuItems.length) {
            return null;
        }

        var items = [];
        menu._menuItems.forEach(function (item) {
            if (!item || (!item.text && !item.href && !item.subMenu)) {
                items.push({ type: 'separator' });
                return;
            }

            var subItems = item.subMenu ? serializeTsMenu(item.subMenu) : null;
            items.push({
                type: subItems && subItems.length ? 'submenu' : 'item',
                label: decodeHtmlEntities(item.text || ''),
                href: normalizeMenuUrl(item.href),
                title: decodeHtmlEntities(item.title || ''),
                icon: item.image ? normalizeNavigationUrl(item.image) : '',
                target: item.blank ? '_blank' : 'mainFrame',
                items: subItems || []
            });
        });

        items = items.filter(function (item, index) {
            if (item.type !== 'separator') {
                return true;
            }
            var prev = items[index - 1];
            var next = items[index + 1];
            return !!prev && !!next && prev.type !== 'separator' && next.type !== 'separator';
        });

        return items.length ? items : null;
    }

    function extractMenuButtonMeta(doc, selector, fallbackLabel) {
        var button = doc ? doc.querySelector(selector) : null;
        var icon = button ? button.querySelector('img') : null;
        return {
            label: textOf(button) || fallbackLabel,
            icon: icon ? normalizeNavigationUrl(icon.getAttribute('src') || icon.src || '') : ''
        };
    }

    function runShellAction(item) {
        closeActionMenus();
        if (!item || !item.href) {
            return;
        }

        if (/^javascript:/i.test(item.href)) {
            try {
                contentFrame.contentWindow.eval(item.href.replace(/^javascript:/i, ''));
            } catch (e) {
                console.warn('Shell action failed:', e);
            }
            return;
        }

        if (item.target === '_self') {
            window.location.href = item.href;
            return;
        }

        window.TS.navigate(item.href);
    }

    function createActionItemNode(item) {
        var node;
        var icon;
        var labelWrap;
        var title;

        if (item.type === 'separator') {
            node = document.createElement('div');
            node.className = 'ts-shell-action-separator';
            return node;
        }

        if (item.type === 'submenu') {
            node = document.createElement('div');
            var toggle = document.createElement('button');
            var submenu = document.createElement('div');
            var caret = document.createElement('span');

            node.className = 'ts-shell-action-submenu-group';
            toggle.className = 'ts-shell-action-submenu-toggle';
            toggle.type = 'button';
            toggle.setAttribute('aria-expanded', 'false');

            if (item.icon) {
                icon = document.createElement('img');
                icon.className = 'ts-shell-action-item-icon';
                icon.src = item.icon;
                icon.alt = '';
                toggle.appendChild(icon);
            }

            labelWrap = document.createElement('span');
            labelWrap.className = 'ts-shell-action-item-label';
            labelWrap.textContent = item.label;
            toggle.appendChild(labelWrap);

            caret = document.createElement('span');
            caret.className = 'ts-shell-action-submenu-caret';
            caret.setAttribute('aria-hidden', 'true');
            caret.textContent = '▸';
            toggle.appendChild(caret);

            submenu.className = 'ts-shell-action-submenu';
            submenu.hidden = true;
            item.items.forEach(function (subItem) {
                submenu.appendChild(createActionItemNode(subItem));
            });

            toggle.addEventListener('click', function (event) {
                event.preventDefault();
                var expanded = toggle.getAttribute('aria-expanded') === 'true';
                toggle.setAttribute('aria-expanded', expanded ? 'false' : 'true');
                submenu.hidden = expanded;
            });

            node.appendChild(toggle);
            node.appendChild(submenu);
            return node;
        }

        node = document.createElement('a');
        node.className = 'ts-shell-action-item';
        node.href = item.href || '#';
        if (item.target === '_blank') {
            node.target = '_blank';
            node.rel = 'noopener noreferrer';
        }

        if (item.icon) {
            icon = document.createElement('img');
            icon.className = 'ts-shell-action-item-icon';
            icon.src = item.icon;
            icon.alt = '';
            node.appendChild(icon);
        }

        labelWrap = document.createElement('span');
        labelWrap.className = 'ts-shell-action-item-label';
        labelWrap.textContent = item.label;
        if (item.title) {
            title = document.createElement('span');
            title.className = 'ts-shell-action-item-title';
            title.textContent = item.title;
            labelWrap.appendChild(title);
        }
        node.appendChild(labelWrap);

        node.addEventListener('click', function (event) {
            event.preventDefault();
            runShellAction(item);
        });

        return node;
    }

    function renderShellActionMenu(slot, config, variant) {
        clearChildren(slot);
        if (!slot || !config || !config.items || !config.items.length) {
            return;
        }

        var wrapper = document.createElement('div');
        var button = document.createElement('button');
        var dropdown = document.createElement('div');
        var list = document.createElement('div');
        var icon;
        var label = document.createElement('span');
        var caret = document.createElement('span');

        wrapper.className = 'ts-shell-action-menu' + ((variant === 'right' || variant === 'create') ? ' ts-shell-action-menu--right' : '');

        button.className = 'ts-shell-action-button' + (variant === 'create' ? ' ts-shell-action-button--create' : '');
        button.type = 'button';
        button.setAttribute('aria-haspopup', 'menu');
        button.setAttribute('aria-expanded', 'false');

        if (config.icon) {
            icon = document.createElement('img');
            icon.className = 'ts-shell-action-button-icon';
            icon.src = config.icon;
            icon.alt = '';
            button.appendChild(icon);
        }

        label.textContent = config.label;
        button.appendChild(label);

        caret.className = 'ts-shell-action-button-caret';
        caret.setAttribute('aria-hidden', 'true');
        caret.textContent = '▾';
        button.appendChild(caret);

        dropdown.className = 'ts-shell-action-dropdown';
        dropdown.hidden = true;
        dropdown.setAttribute('role', 'menu');

        list.className = 'ts-shell-action-dropdown-list';
        config.items.forEach(function (item) {
            list.appendChild(createActionItemNode(item));
        });
        dropdown.appendChild(list);

        button.addEventListener('click', function (event) {
            event.preventDefault();
            var expanded = button.getAttribute('aria-expanded') === 'true';
            closeActionMenus();
            if (!expanded) {
                dropdown.hidden = false;
                button.setAttribute('aria-expanded', 'true');
            }
        });

        wrapper.appendChild(button);
        wrapper.appendChild(dropdown);
        slot.appendChild(wrapper);
    }

    function applyShellContextConfig(config) {
        closeActionMenus();
        renderShellActionMenu(primaryActionSlot, config ? config.primary : null, 'leading');
        renderShellActionMenu(createActionSlot, config ? config.create : null, 'create');
    }

    function destroyShellAutocomplete() {
        if (!window.jQuery || !window.jQuery.fn || typeof window.jQuery.fn.autocomplete !== 'function') {
            return;
        }
        window.jQuery(searchInput).off('.tsShellAutocomplete');
        try {
            window.jQuery(searchInput).autocomplete('destroy');
        } catch (ignored) {
            // no-op
        }
    }

    function canSubmitSearch() {
        if (!currentSearchConfig) {
            return false;
        }
        return !!(searchInput.value && searchInput.value.replace(/\s+/g, ' ').trim());
    }

    function submitShellSearch() {
        if (!canSubmitSearch()) {
            return false;
        }
        if (typeof searchForm.requestSubmit === 'function') {
            searchForm.requestSubmit();
        } else {
            searchForm.submit();
        }
        return true;
    }

    function initShellAutocomplete() {
        var $ = window.jQuery;
        if (!currentSearchConfig || !$ || !$.fn || typeof $.fn.autocomplete !== 'function' || !window.TSPredictor) {
            destroyShellAutocomplete();
            return;
        }

        destroyShellAutocomplete();

        var config = currentSearchConfig;
        var input = $(searchInput);
        input.on('keydown.tsShellAutocomplete', function (event) {
            if ($.ui && event.keyCode === $.ui.keyCode.ENTER) {
                submitShellSearch();
            }
        }).autocomplete({
            source: function (request, response) {
                $.ajax({
                    url: config.predictorUrl,
                    dataType: 'json',
                    html: true,
                    data: {
                        key: request.term,
                        session: config.session,
                        id: config.contextId
                    },
                    success: function (data) {
                        response(data);
                    }
                });
            },
            minLength: 1,
            delay: 300,
            open: function () {
                $(this).autocomplete('widget').addClass('ts-header-search-menu');
            },
            select: function (event, ui) {
                var value = window.TSPredictor.rawValue(ui.item.value);
                if (window.TSPredictor.isSearchRequestItem(value, ui.item.label)) {
                    submitShellSearch();
                } else if (value.indexOf('_u-') > -1) {
                    window.TS.navigate(config.contextPath + '/UserAction.do?method=page&id=' +
                        window.TSPredictor.safeToken(window.TSPredictor.extractToken(value, '_u-')));
                } else if (/^u-/.test(value)) {
                    window.TS.navigate(config.contextPath + '/UserAction.do?method=page&id=' +
                        window.TSPredictor.safeToken(window.TSPredictor.extractToken(value, 'u-')));
                } else if (value.indexOf('_') > -1) {
                    window.TS.navigate(config.contextPath + '/TaskAction.do?method=page&id=' +
                        window.TSPredictor.safeToken(window.TSPredictor.extractToken(value, '_')));
                } else if (config.searchIn === 'users') {
                    window.TS.navigate(config.contextPath + '/TaskAction.do?method=page&id=' +
                        window.TSPredictor.safeToken(value));
                } else {
                    submitShellSearch();
                }
                return false;
            },
            focus: function (event, ui) {
                var key = window.TSPredictor.key(ui.item.value, ui.item.label);
                if (key) {
                    searchInput.value = key;
                }
                return false;
            }
        });

        var autocompleteInstance = null;
        try {
            autocompleteInstance = input.autocomplete('instance');
        } catch (ignored) {
            autocompleteInstance = input.data('ui-autocomplete') || input.data('autocomplete');
        }
        if (autocompleteInstance) {
            autocompleteInstance._renderItem = function (ul, item) {
                return window.TSPredictor.renderItem(ul, item, {
                    inputSelector: '#tsShellSearchInput',
                    queryLabel: config.queryLabel,
                    searchPrefix: config.searchPrefix
                });
            };
        }
    }

    function applySearchConfig(config) {
        currentSearchConfig = config;
        clearChildren(searchHidden);

        if (!config) {
            destroyShellAutocomplete();
            searchForm.action = buildAbsoluteAppUrl('/TaskDispatchAction.do');
            searchForm.method = 'post';
            searchInput.value = '';
            searchInput.disabled = true;
            searchInput.placeholder = 'Поиск недоступен';
            return;
        }

        searchForm.action = config.actionUrl;
        searchForm.method = config.method;
        searchForm.target = 'mainFrame';
        config.hiddenFields.forEach(function (field) {
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = field.name;
            input.value = field.value;
            searchHidden.appendChild(input);
        });
        searchInput.disabled = false;
        searchInput.value = config.value;
        searchInput.placeholder = config.placeholder;
        searchInput.setAttribute('aria-label', config.queryLabel);
        initShellAutocomplete();
    }

    function extractSearchConfig(doc, banner) {
        var form = banner ? banner.querySelector('#searchForm') : null;
        var input = form ? form.querySelector('#key') : null;
        if (!form || !input) {
            return null;
        }

        var actionUrl = normalizeNavigationUrl(form.action || form.getAttribute('action') || buildAbsoluteAppUrl('/TaskDispatchAction.do'));
        var actionPath = new URL(actionUrl, window.location.origin).pathname;
        var slashPos = actionPath.lastIndexOf('/');
        var contextPath = slashPos > 0 ? actionPath.substring(0, slashPos) : '';
        var hiddenFields = [];
        var hiddenInputs = form.querySelectorAll('input[type="hidden"][name]');
        var searchIn = '';
        var contextId = '';
        var session = '';

        hiddenInputs.forEach(function (field) {
            hiddenFields.push({
                name: field.name,
                value: field.value
            });
            if (field.name === 'searchIn') {
                searchIn = field.value;
            } else if (field.name === 'id') {
                contextId = field.value;
            } else if (field.name === 'session') {
                session = field.value;
            }
        });

        return {
            actionUrl: actionUrl,
            method: (form.getAttribute('method') || 'post').toLowerCase(),
            hiddenFields: hiddenFields,
            value: input.value || '',
            queryLabel: input.getAttribute('aria-label') || 'Search',
            searchPrefix: (input.getAttribute('aria-label') || 'Search') + ': ',
            placeholder: searchIn === 'users' ? 'Поиск по пользователям и задачам' : 'Поиск по задачам и людям',
            searchIn: searchIn,
            contextId: contextId,
            session: session,
            contextPath: contextPath || APP_CONTEXT_PATH,
            predictorUrl: window.location.origin + (contextPath || APP_CONTEXT_PATH || '') + '/predictor/'
        };
    }

    function extractUserConfig(banner) {
        if (!banner) {
            return null;
        }

        var userLink = banner.querySelector('.ts-header-user-link');
        var roles = banner.querySelector('.ts-header-user-roles');
        var actions = [];

        banner.querySelectorAll('.ts-header-action-link').forEach(function (link) {
            var href = link.getAttribute('href') || link.href || '';
            actions.push({
                label: textOf(link),
                href: /^javascript:/i.test(href) ? href : normalizeNavigationUrl(href),
                target: href.indexOf('LoginAction.do') > -1 ? '_self' : 'mainFrame'
            });
        });

        return {
            name: textOf(userLink),
            profileLabel: textOf(userLink),
            profileHref: userLink ? (userLink.getAttribute('href') || userLink.href || '') : '',
            role: textOf(roles),
            actions: actions
        };
    }

    function extractTaskShellActions(doc, win) {
        if (!doc || !win || !doc.querySelector('.ts-task-context')) {
            return null;
        }

        var primaryItems = serializeTsMenu(win.tsMenu);
        var createItems = serializeTsMenu(win.taskCreateMenu);
        var primaryMeta = extractMenuButtonMeta(doc, '.mainmenu > a.menubut', 'Управление задачами');
        var createMeta = extractMenuButtonMeta(doc, '.ts-task-create-menu > a.menubut', 'Создать');

        return {
            primary: primaryItems ? {
                label: primaryMeta.label,
                icon: primaryMeta.icon,
                items: primaryItems
            } : null,
            create: createItems ? {
                label: createMeta.label,
                icon: createMeta.icon,
                items: createItems
            } : null
        };
    }

    function ensureEmbeddedShellStyles(doc) {
        if (!doc || !doc.head || doc.getElementById('ts-shell-embedded-style')) {
            return;
        }
        var style = doc.createElement('style');
        style.id = 'ts-shell-embedded-style';
        style.textContent =
            '.login[role="banner"]{display:none !important;}' +
            '.controlPanel #topleft{display:none !important;}';
        doc.head.appendChild(style);
    }

    function applyEmbeddedTaskActionPromotion(doc, enabled) {
        if (!doc || !doc.head) {
            return;
        }

        var style = doc.getElementById('ts-shell-task-promoted-style');
        if (!enabled) {
            if (style && style.parentNode) {
                style.parentNode.removeChild(style);
            }
            return;
        }

        if (!style) {
            style = doc.createElement('style');
            style.id = 'ts-shell-task-promoted-style';
            doc.head.appendChild(style);
        }

        style.textContent =
            '.ts-task-header-main-group,.ts-task-toolbar-divider,.ts-task-create-menu{display:none !important;}' +
            'div.controlPanel.ts-task-header-toolbar{justify-content:flex-end !important;}';
    }

    function syncShellHeader() {
        try {
            var doc = contentFrame.contentDocument;
            var win = contentFrame.contentWindow;
            var banner = doc.querySelector('.login[role="banner"], .login.header[role="banner"]');
            var brand = banner ? banner.querySelector('.ts-header-brand-link') : null;

            ensureEmbeddedShellStyles(doc);

            if (brand) {
                brandLink.href = /^javascript:/i.test(brand.getAttribute('href') || '')
                    ? (brand.getAttribute('href') || '')
                    : normalizeNavigationUrl(brand.href || brand.getAttribute('href'));
            } else {
                brandLink.href = buildAbsoluteAppUrl('/TaskAction.do');
            }
            brandLink.target = 'mainFrame';

            applySearchConfig(extractSearchConfig(doc, banner));

            var userConfig = extractUserConfig(banner);
            if (userConfig) {
                lastUserConfig = userConfig;
            }
            applyUserConfig(lastUserConfig);
            var taskShellActions = extractTaskShellActions(doc, win);
            applyShellContextConfig(taskShellActions);
            applyEmbeddedTaskActionPromotion(doc, !!(taskShellActions && (taskShellActions.primary || taskShellActions.create)));
            closeUserMenu();

            if (banner) {
                banner.style.display = 'none';
            }

            if (doc && doc.body) {
                doc.body.setAttribute('data-shell-embedded', 'true');
            }

            var path = win.location.pathname + win.location.search;
            var innerTitle = doc.title;
            if (innerTitle) {
                document.title = innerTitle;
            }
            if (path && path !== '/app-shell.html') {
                history.replaceState({ path: path }, innerTitle || '', path);
            }
        } catch (e) {
            console.warn('Shell header sync failed:', e);
        }
    }

    var params = new URLSearchParams(window.location.search);
    var initialUrl = params.get('url');
    brandLink.href = buildAbsoluteAppUrl('/TaskAction.do');

    if (initialUrl) {
        if (isValidInternalUrl(initialUrl)) {
            contentFrame.src = normalizeNavigationUrl(initialUrl);
        } else {
            console.warn('Blocked potentially unsafe URL:', initialUrl);
        }
    } else {
        contentFrame.src = buildAbsoluteAppUrl('/TaskAction.do');
    }

    toggleButton.addEventListener('click', function () {
        toggleSidebar();
    });

    toggleButton.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            toggleSidebar();
        }
    });

    if (backdrop) {
        backdrop.addEventListener('click', function () {
            if (sidebarOpen) {
                setSidebarState(false);
            }
        });
    }

    userButton.addEventListener('click', function () {
        toggleUserMenu();
    });

    document.addEventListener('click', function (event) {
        if (!document.getElementById('tsShellUser').contains(event.target)) {
            closeUserMenu();
        }
        if (!event.target.closest || !event.target.closest('.ts-shell-action-menu')) {
            closeActionMenus();
        }
    });

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape') {
            closeUserMenu();
            closeActionMenus();
            if (sidebarOpen && isTabletOrSmaller()) {
                setSidebarState(false);
            }
        }
    });

    searchForm.addEventListener('submit', function (event) {
        if (!canSubmitSearch()) {
            event.preventDefault();
        }
    });

    sidebarHandle.addEventListener('mousedown', function (event) {
        if (!sidebarOpen || isTabletOrSmaller()) {
            return;
        }
        event.preventDefault();
        dragging = true;
        sidebarHandle.classList.add('dragging');
        shell.classList.add('resizing');
    });

    document.addEventListener('mousemove', function (event) {
        if (!dragging) {
            return;
        }
        var newWidth = event.clientX;
        if (newWidth < MIN_WIDTH) {
            newWidth = MIN_WIDTH;
        }
        if (newWidth > MAX_WIDTH) {
            newWidth = MAX_WIDTH;
        }
        sidebarWidth = newWidth;
        shellBody.style.gridTemplateColumns = sidebarWidth + 'px ' + HANDLE_WIDTH + 'px 1fr';
    });

    document.addEventListener('mouseup', function () {
        if (!dragging) {
            return;
        }
        dragging = false;
        sidebarHandle.classList.remove('dragging');
        shell.classList.remove('resizing');
        localStorage.setItem('ts-sidebar-width', sidebarWidth);
        applyLayout();
    });

    var lastWasTablet = isTabletOrSmaller();
    window.addEventListener('resize', function () {
        var currentIsTablet = isTabletOrSmaller();
        if (lastWasTablet !== currentIsTablet) {
            applyLayout();
        }
        lastWasTablet = currentIsTablet;
    });

    window.TS.navigate = function (url) {
        var target = normalizeNavigationUrl(url);
        if (target) {
            contentFrame.src = target;
        }
    };

    window.TS.tree = {
        _call: function (fnName, args) {
            try {
                var win = sidebarFrame.contentWindow;
                if (win && typeof win[fnName] === 'function') {
                    return win[fnName].apply(win, args);
                }
            } catch (e) {
                console.warn('TS.tree.' + fnName + ' failed:', e);
            }
        },
        reload: function (hint) { this._call('reloadTsTree', [hint]); },
        reloadUser: function (hint, nodes) { this._call('reloadTsUserTree', [hint, nodes]); },
        expand: function (path) { this._call('expandTsTree', [path]); },
        selectNodes: function (nodes) { this._call('selectNodesTsTree', [nodes]); },
        selectUsers: function (nodes) { this._call('selectUsersTsTree', [nodes]); },
        updateBookmarks: function (url) { this._call('updateBookmarks', [url]); },
        isLoaded: function () {
            try {
                return sidebarFrame.contentWindow.TREE_LOADED === true;
            } catch (e) {
                return false;
            }
        }
    };

    contentFrame.addEventListener('load', function () {
        syncShellHeader();
    });

    (function () {
        var touchStartX = 0;
        var touchStartY = 0;
        var SWIPE_THRESHOLD = 50;
        var EDGE_ZONE = 30;

        document.addEventListener('touchstart', function (event) {
            var touch = event.touches[0];
            touchStartX = touch.clientX;
            touchStartY = touch.clientY;
        }, { passive: true });

        document.addEventListener('touchend', function (event) {
            if (!isTabletOrSmaller()) {
                return;
            }

            var touch = event.changedTouches[0];
            var deltaX = touch.clientX - touchStartX;
            var deltaY = touch.clientY - touchStartY;

            if (Math.abs(deltaX) < SWIPE_THRESHOLD || Math.abs(deltaY) > Math.abs(deltaX)) {
                return;
            }

            if (deltaX > 0 && !sidebarOpen && touchStartX < EDGE_ZONE) {
                setSidebarState(true);
            } else if (deltaX < 0 && sidebarOpen) {
                setSidebarState(false);
            }
        }, { passive: true });
    })();

    applyLayout();
    applyUserConfig(null);
    applySearchConfig(null);
})();
