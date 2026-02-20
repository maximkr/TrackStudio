(function () {
    'use strict';

    // === Global namespace for cross-component communication ===
    window.TS = window.TS || {};

    var shell = document.getElementById('tsShell');
    var sidebarFrame = document.getElementById('tsSidebarFrame');
    var contentFrame = document.getElementById('tsContent');
    var toggle = document.getElementById('tsSidebarToggle');

    var DEFAULT_WIDTH = 260;
    var MIN_WIDTH = 150;
    var MAX_WIDTH = 600;
    var HANDLE_WIDTH = 6;

    // --- Deep link: ?url= parameter ---
    var params = new URLSearchParams(window.location.search);
    var initialUrl = params.get('url');
    if (initialUrl) {
        contentFrame.src = decodeURIComponent(initialUrl);
    }

    // --- Sidebar state ---
    var sidebarWidth = parseInt(localStorage.getItem('ts-sidebar-width'), 10) || DEFAULT_WIDTH;
    var sidebarOpen = localStorage.getItem('ts-sidebar') !== 'closed';

    function applyColumns() {
        var w = sidebarOpen ? sidebarWidth : 0;
        shell.style.gridTemplateColumns = w + 'px ' + HANDLE_WIDTH + 'px 1fr';
        if (sidebarOpen) {
            shell.classList.remove('sidebar-collapsed');
        } else {
            shell.classList.add('sidebar-collapsed');
        }
    }

    applyColumns();

    // --- Toggle (double-click or click on collapsed handle) ---
    function toggleSidebar() {
        sidebarOpen = !sidebarOpen;
        localStorage.setItem('ts-sidebar', sidebarOpen ? 'open' : 'closed');
        applyColumns();
    }

    toggle.addEventListener('dblclick', function () {
        toggleSidebar();
    });

    // Click on collapsed handle → expand
    toggle.addEventListener('click', function () {
        if (!sidebarOpen) {
            toggleSidebar();
        }
    });

    window.TS.sidebar = {
        toggle: toggleSidebar,
        isOpen: function () { return sidebarOpen; },
        open: function () { if (!sidebarOpen) toggleSidebar(); },
        close: function () { if (sidebarOpen) toggleSidebar(); }
    };

    // --- Drag to resize ---
    var dragging = false;

    toggle.addEventListener('mousedown', function (e) {
        if (!sidebarOpen) return; // collapsed — click only
        e.preventDefault();
        dragging = true;
        toggle.classList.add('dragging');
        shell.classList.add('resizing');
    });

    document.addEventListener('mousemove', function (e) {
        if (!dragging) return;
        var newWidth = e.clientX;
        if (newWidth < MIN_WIDTH) newWidth = MIN_WIDTH;
        if (newWidth > MAX_WIDTH) newWidth = MAX_WIDTH;
        sidebarWidth = newWidth;
        shell.style.gridTemplateColumns = sidebarWidth + 'px ' + HANDLE_WIDTH + 'px 1fr';
    });

    document.addEventListener('mouseup', function () {
        if (!dragging) return;
        dragging = false;
        toggle.classList.remove('dragging');
        shell.classList.remove('resizing');
        localStorage.setItem('ts-sidebar-width', sidebarWidth);
    });

    // --- Navigation ---
    window.TS.navigate = function (url) {
        contentFrame.src = url;
    };

    // --- Tree API bridge ---
    // Proxies calls into the TreeFrame iframe
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
            } catch (e) { return false; }
        }
    };

    // --- URL sync: update browser URL when navigating inside iframe ---
    contentFrame.addEventListener('load', function () {
        try {
            var path = contentFrame.contentWindow.location.pathname +
                       contentFrame.contentWindow.location.search;
            // Update title
            var innerTitle = contentFrame.contentDocument.title;
            if (innerTitle) document.title = innerTitle;
            // Update URL in address bar (without reload)
            if (path && path !== '/app-shell.html') {
                history.replaceState({path: path}, innerTitle || '', path);
            }
        } catch (e) {
            // cross-origin or security error — ignore
        }
    });

    // --- Backward compatibility ---
    // self.top.frames[0] = sidebar iframe (automatic, iframes register in DOM order)
    // self.top.frames[1] = content iframe (automatic)
    // No additional bridging needed for existing self.top.frames[0/1] code.

})();
