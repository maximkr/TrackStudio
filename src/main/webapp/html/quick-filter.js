(function () {
    function onReady(fn) {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', fn);
        } else {
            fn();
        }
    }

    function initQuickFilter() {
        var input = document.getElementById('quickFilter');
        var table = document.getElementById('taskListTable');
        if (!input || !table) {
            return;
        }

        var rows = table.getElementsByTagName('tr');
        var primaryRows = [];
        for (var i = 0; i < rows.length; i++) {
            if (rows[i].getAttribute('data-task-row') === 'true') {
                primaryRows.push(rows[i]);
            }
        }
        if (!primaryRows.length) {
            return;
        }

        var cachedText = [];
        for (var j = 0; j < primaryRows.length; j++) {
            cachedText[j] = (primaryRows[j].textContent || primaryRows[j].innerText || '').toLowerCase();
        }

        function setGroupVisible(row, visible) {
            row.style.display = visible ? '' : 'none';
            var next = row.nextSibling;
            while (next) {
                if (next.nodeType === 1 && next.tagName === 'TR') {
                    if (next.getAttribute('data-task-row') === 'true') {
                        break;
                    }
                    next.style.display = visible ? '' : 'none';
                }
                next = next.nextSibling;
            }
        }

        function applyFilter() {
            var value = input.value || '';
            value = value.trim().toLowerCase();
            for (var k = 0; k < primaryRows.length; k++) {
                if (!value) {
                    setGroupVisible(primaryRows[k], true);
                } else {
                    var match = cachedText[k].indexOf(value) !== -1;
                    setGroupVisible(primaryRows[k], match);
                }
            }
        }

        input.addEventListener('input', applyFilter);
    }

    function findAncestorWithAttr(el, attr) {
        while (el && el !== document.documentElement) {
            if (el.getAttribute && el.getAttribute(attr)) {
                return el;
            }
            el = el.parentNode;
        }
        return null;
    }

    function findParentForm(el) {
        while (el && el !== document.documentElement) {
            if (el.tagName === 'FORM') {
                return el;
            }
            el = el.parentNode;
        }
        return null;
    }

    function initBulkBar() {
        var bulkBar = document.getElementById('bulkBar');
        if (!bulkBar) {
            return;
        }
        var countEl = document.getElementById('bulkCount');
        var formId = bulkBar.getAttribute('data-form');
        var form = formId ? document.getElementById(formId) : null;
        if (!form) {
            form = bulkBar.closest ? bulkBar.closest('form') : findParentForm(bulkBar);
        }

        function update() {
            var checked = document.querySelectorAll('input[name="SELTASK"]:checked');
            var count = checked.length;
            if (countEl) {
                countEl.textContent = String(count);
            }
            bulkBar.style.display = count > 0 ? 'flex' : 'none';
        }

        function scheduleUpdate(target) {
            if (!target) {
                return;
            }
            if (target.id === 'headerChecker' || target.name === 'SELTASK') {
                setTimeout(update, 0);
            }
        }

        document.addEventListener('click', function (event) {
            scheduleUpdate(event.target || event.srcElement);
        });
        document.addEventListener('change', function (event) {
            scheduleUpdate(event.target || event.srcElement);
        });

        bulkBar.addEventListener('click', function (event) {
            var target = event.target || event.srcElement;
            var button = findAncestorWithAttr(target, 'data-bulk-action');
            if (!button) {
                return;
            }
            var action = button.getAttribute('data-bulk-action');
            var selector = null;
            if (action === 'archive') {
                selector = 'input[name="ARCHIVE"]';
            } else if (action === 'cut') {
                selector = 'input[name="CUT"]';
            } else if (action === 'copy') {
                selector = 'input[name="SINGLE_COPY"]';
            } else if (action === 'copy-recursively') {
                selector = 'input[name="RECURSIVELY_COPY"]';
            } else if (action === 'delete') {
                selector = 'input[name="DELETE"]';
            }
            if (!selector) {
                return;
            }
            var scope = form || document;
            var trigger = scope.querySelector(selector);
            if (trigger && !trigger.disabled) {
                trigger.click();
            }
        });

        update();
    }

    function formatRelativeTime(diffMs, rtf) {
        var seconds = Math.round(diffMs / 1000);
        var absSeconds = Math.abs(seconds);
        var unit = 'second';
        var value = seconds;

        if (absSeconds >= 60 && absSeconds < 3600) {
            unit = 'minute';
            value = Math.round(seconds / 60);
        } else if (absSeconds >= 3600 && absSeconds < 86400) {
            unit = 'hour';
            value = Math.round(seconds / 3600);
        } else if (absSeconds >= 86400 && absSeconds < 604800) {
            unit = 'day';
            value = Math.round(seconds / 86400);
        } else if (absSeconds >= 604800 && absSeconds < 2592000) {
            unit = 'week';
            value = Math.round(seconds / 604800);
        } else if (absSeconds >= 2592000 && absSeconds < 31536000) {
            unit = 'month';
            value = Math.round(seconds / 2592000);
        } else if (absSeconds >= 31536000) {
            unit = 'year';
            value = Math.round(seconds / 31536000);
        }

        if (rtf && rtf.format) {
            return rtf.format(value, unit);
        }

        var absValue = Math.abs(value);
        var suffix = value < 0 ? ' ago' : '';
        var prefix = value > 0 ? 'in ' : '';
        return prefix + absValue + ' ' + unit + (absValue === 1 ? '' : 's') + suffix;
    }

    function initRelativeTimes() {
        var items = document.querySelectorAll('.ts-relative-time[data-time]');
        if (!items.length) {
            return;
        }

        var appLocale = window.locale || 'en';
        var rtf = null;
        if (window.Intl && Intl.RelativeTimeFormat) {
            try {
                rtf = new Intl.RelativeTimeFormat(appLocale, { numeric: 'auto' });
            } catch (err) {
                rtf = new Intl.RelativeTimeFormat('en', { numeric: 'auto' });
            }
        }

        function update() {
            var now = Date.now();
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var value = item.getAttribute('data-time');
                if (!value) {
                    continue;
                }
                var ts = Number(value);
                if (isNaN(ts)) {
                    continue;
                }
                var diff = ts - now;
                item.setAttribute('data-sort', String(ts));
                var sortKey = item.querySelector('.ts-relative-time__sortkey');
                if (sortKey) {
                    sortKey.textContent = String(ts);
                }
                var label = item.querySelector('.ts-relative-time__label');
                if (!label) {
                    label = document.createElement('span');
                    label.className = 'ts-relative-time__label';
                    item.appendChild(label);
                }
                label.textContent = formatRelativeTime(diff, rtf);
            }
        }

        update();
        // H7: Store interval ID for cleanup to prevent memory leaks
        var updateIntervalId = setInterval(update, 60000);
        
        // Cleanup interval on page unload to prevent memory leaks in iframe navigation
        if (window.addEventListener) {
            window.addEventListener('beforeunload', function() {
                clearInterval(updateIntervalId);
            });
        }
    }

    function initPriorityColors() {
        var cells = document.querySelectorAll('td.ts-priority-cell');
        if (!cells.length) {
            return;
        }

        function hashString(str) {
            var hash = 0;
            for (var i = 0; i < str.length; i++) {
                hash = ((hash << 5) - hash) + str.charCodeAt(i);
                hash |= 0;
            }
            return Math.abs(hash);
        }

        function clamp01(value) {
            if (value < 0) {
                return 0;
            }
            if (value > 1) {
                return 1;
            }
            return value;
        }

        function colorFromOrder(order, minOrder, defaultOrder, maxOrder) {
            // 120 = green, 55 = yellow, 0 = red.
            var greenHue = 120;
            var yellowHue = 55;
            var redHue = 0;
            var hue = yellowHue;

            if (order <= defaultOrder) {
                if (defaultOrder > minOrder) {
                    var lowRatio = clamp01((order - minOrder) / (defaultOrder - minOrder));
                    hue = Math.round(greenHue + ((yellowHue - greenHue) * lowRatio));
                }
            } else if (maxOrder > defaultOrder) {
                var highRatio = clamp01((order - defaultOrder) / (maxOrder - defaultOrder));
                hue = Math.round(yellowHue + ((redHue - yellowHue) * highRatio));
            }
            return 'hsl(' + hue + ', 78%, 45%)';
        }

        var orders = [];
        var defaultOrders = [];
        for (var k = 0; k < cells.length; k++) {
            var orderAttr = cells[k].getAttribute('data-priority-order');
            if (!orderAttr) {
                continue;
            }
            var orderValue = Number(orderAttr);
            if (!isNaN(orderValue)) {
                orders.push(orderValue);
                var isDefault = String(cells[k].getAttribute('data-priority-default') || '').toLowerCase() === 'true';
                if (isDefault) {
                    defaultOrders.push(orderValue);
                }
            }
        }

        var minOrder = 0;
        var maxOrder = 0;
        if (orders.length) {
            minOrder = Math.min.apply(null, orders);
            maxOrder = Math.max.apply(null, orders);
        }
        var defaultOrder = minOrder;
        if (defaultOrders.length) {
            defaultOrder = defaultOrders[0];
        } else if (orders.length) {
            defaultOrder = minOrder + ((maxOrder - minOrder) / 2);
        }

        for (var j = 0; j < cells.length; j++) {
            var cell = cells[j];
            if (cell.style.getPropertyValue('--ts-priority-color')) {
                continue;
            }
            var color = null;
            var cellOrder = Number(cell.getAttribute('data-priority-order'));
            if (!isNaN(cellOrder) && orders.length) {
                color = colorFromOrder(cellOrder, minOrder, defaultOrder, maxOrder);
            } else {
                var label = cell.getAttribute('data-priority') || '';
                label = label.trim();
                if (!label) {
                    continue;
                }
                var fallbackPalette = ['#27ae60', '#2ecc71', '#f1c40f', '#f39c12', '#e67e22', '#e74c3c', '#c0392b'];
                color = fallbackPalette[hashString(label) % fallbackPalette.length];
            }
            cell.style.setProperty('--ts-priority-color', color);
        }
    }

    onReady(function () {
        initQuickFilter();
        initBulkBar();
        initRelativeTimes();
        initPriorityColors();
    });
})();
