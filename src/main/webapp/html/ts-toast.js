/**
 * TSToast — lightweight toast notification system.
 *
 * Phase 10: Polish & Accessibility
 *
 * Usage:
 *   TSToast.show("Operation completed");
 *   TSToast.success("Task created successfully");
 *   TSToast.error("Failed to save changes");
 *   TSToast.warning("Connection unstable");
 *
 * Options:
 *   TSToast.show("Message", { duration: 5000, type: "success" });
 */
(function () {
    'use strict';

    var DEFAULTS = {
        duration: 4000,    // ms before auto-dismiss (0 = manual dismiss only)
        type: '',          // '', 'success', 'error', 'warning'
        dismissible: true  // click to dismiss
    };

    // Detect if running inside an iframe
    function getTargetDoc() {
        try {
            if (window.self !== window.top) {
                return window.parent.document;
            }
        } catch (e) {
            // Cross-origin access denied - use local document
        }
        return document;
    }

    function getContainer() {
        var targetDoc = getTargetDoc();
        var container;
        try {
            container = targetDoc.getElementById('tsToastContainer');
        } catch (e) {
            // Fallback to local document if cross-origin
            targetDoc = document;
            container = document.getElementById('tsToastContainer');
        }
        if (!container) {
            container = targetDoc.createElement('div');
            container.id = 'tsToastContainer';
            container.className = 'ts-toast-container';
            container.setAttribute('aria-live', 'polite');
            container.setAttribute('aria-atomic', 'false');
            targetDoc.body.appendChild(container);
        }
        return container;
    }

    function dismiss(el) {
        if (el._dismissed) return;
        el._dismissed = true;
        el.classList.add('ts-toast--out');
        el.addEventListener('animationend', function () {
            if (el.parentNode) {
                el.parentNode.removeChild(el);
            }
        });
    }

    function show(message, opts) {
        opts = opts || {};
        var duration = opts.duration !== undefined ? opts.duration : DEFAULTS.duration;
        var type = opts.type || DEFAULTS.type;
        var dismissible = opts.dismissible !== undefined ? opts.dismissible : DEFAULTS.dismissible;

        var container = getContainer();

        var el = document.createElement('div');
        el.className = 'ts-toast';
        if (type) {
            el.className += ' ts-toast--' + type;
        }
        el.setAttribute('role', 'status');
        el.textContent = message;

        if (dismissible) {
            el.style.cursor = 'pointer';
            el.addEventListener('click', function () {
                dismiss(el);
            });
        }

        container.appendChild(el);

        if (duration > 0) {
            setTimeout(function () {
                dismiss(el);
            }, duration);
        }

        return {
            dismiss: function () { dismiss(el); },
            element: el
        };
    }

    window.TSToast = {
        show: function (message, opts) {
            return show(message, opts);
        },
        success: function (message, opts) {
            return show(message, Object.assign({}, opts, { type: 'success' }));
        },
        error: function (message, opts) {
            return show(message, Object.assign({}, opts, { type: 'error' }));
        },
        warning: function (message, opts) {
            return show(message, Object.assign({}, opts, { type: 'warning' }));
        }
    };
})();
