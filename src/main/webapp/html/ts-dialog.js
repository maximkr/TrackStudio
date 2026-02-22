/**
 * TSDialog — drop-in replacement for YAHOO.widget.Dialog.
 *
 * Provides:
 *   new TSDialog(elementId, config)  — wraps an existing DOM element as a modal dialog
 *   TSDialog.confirm(message, cb, options) — one-liner confirmation dialog (replaces window.confirm)
 *   TSDialog.alert(message, cb)      — one-liner alert dialog (replaces window.alert)
 *
 * Config keys (compatible with YUI Dialog):
 *   width            : CSS width string, e.g. "600px"
 *   visible          : boolean (default false)
 *   fixedcenter      : ignored (always centered via CSS)
 *   constraintoviewport : ignored (handled via max-width/max-height)
 *   buttons          : [{text: "OK", handler: fn, isDefault: true}, ...]
 */

(function (window) {
    "use strict";

    // ── Helpers ───────────────────────────────────────────────
    var OVERLAY_CLASS  = "ts-dialog-overlay";
    var DIALOG_CLASS   = "ts-dialog";
    var VISIBLE_CLASS  = "visible";

    function createElement(tag, className, parent) {
        var el = document.createElement(tag);
        if (className) el.className = className;
        if (parent) parent.appendChild(el);
        return el;
    }

    // ── TSDialog constructor ─────────────────────────────────
    function TSDialog(elementId, config) {
        this.id = elementId;
        this.element = document.getElementById(elementId);
        this.config = config || {};
        this.overlay = null;
        this.wrapper = null;
        this._buttons = [];
        this._escHandler = null;
        this._rendered = false;
    }

    /**
     * render() — builds the overlay/wrapper structure around the existing element.
     * Safe to call multiple times (no-op after first).
     */
    TSDialog.prototype.render = function () {
        if (this._rendered) return;

        var el = this.element;
        if (!el) {
            // Element not found — nothing to render; allow retry later
            return;
        }
        this._rendered = true;

        // Create overlay backdrop
        this.overlay = createElement("div", OVERLAY_CLASS);
        document.body.appendChild(this.overlay);

        // Wrap existing element in a dialog container
        this.wrapper = createElement("div", DIALOG_CLASS);
        if (this.config.width) {
            this.wrapper.style.width = this.config.width;
        }

        // Move the element inside the wrapper
        el.parentNode.insertBefore(this.wrapper, el);
        this.wrapper.appendChild(el);
        el.style.visibility = "visible";
        el.style.display = "block";

        // Build footer with buttons
        if (this.config.buttons && this.config.buttons.length > 0) {
            this._buildButtons();
        }

        // Click overlay to close
        var self = this;
        this.overlay.addEventListener("click", function () {
            self.hide();
        });

        // Escape key to close
        this._escHandler = function (e) {
            if (e.key === "Escape" || e.keyCode === 27) {
                self.hide();
            }
        };
    };

    TSDialog.prototype._buildButtons = function () {
        var footer = this.element.querySelector(".ft");
        if (!footer) {
            footer = createElement("div", "ft");
            this.element.appendChild(footer);
        }
        footer.className = "ft ts-dialog-buttons";
        footer.innerHTML = "";

        var buttons = this.config.buttons;
        for (var i = 0; i < buttons.length; i++) {
            var cfg = buttons[i];
            var btn = createElement("button", "ts-dialog-btn");
            btn.textContent = cfg.text;
            if (cfg.isDefault) {
                btn.classList.add("ts-dialog-btn-primary");
            }
            // Bind handler with dialog as `this` context (YUI compat)
            (function (handler, dialog) {
                btn.addEventListener("click", function () {
                    if (typeof handler === "function") {
                        handler.call(dialog);
                    }
                });
            })(cfg.handler, this);
            footer.appendChild(btn);
        }
    };

    TSDialog.prototype.show = function () {
        if (!this._rendered) this.render();
        if (!this.wrapper || !this.overlay) return; // element missing, skip

        this.overlay.classList.add(VISIBLE_CLASS);
        this.wrapper.classList.add(VISIBLE_CLASS);
        document.addEventListener("keydown", this._escHandler);

        // Focus first input or default button
        var self = this;
        setTimeout(function () {
            var focusTarget = self.wrapper.querySelector(
                "input[type=text]:not([type=hidden]), textarea, select, .ts-dialog-btn-primary"
            );
            if (focusTarget) focusTarget.focus();
        }, 50);
    };

    TSDialog.prototype.hide = function () {
        if (this.overlay) this.overlay.classList.remove(VISIBLE_CLASS);
        if (this.wrapper) this.wrapper.classList.remove(VISIBLE_CLASS);
        document.removeEventListener("keydown", this._escHandler);
    };

    // Alias for compatibility
    TSDialog.prototype.cancel = TSDialog.prototype.hide;

    /**
     * destroy() — remove overlay and wrapper, put element back.
     */
    TSDialog.prototype.destroy = function () {
        this.hide();
        if (this.wrapper && this.wrapper.parentNode) {
            this.wrapper.parentNode.insertBefore(this.element, this.wrapper);
            this.wrapper.parentNode.removeChild(this.wrapper);
        }
        if (this.overlay && this.overlay.parentNode) {
            this.overlay.parentNode.removeChild(this.overlay);
        }
        this._rendered = false;
    };


    // ── Static helpers ───────────────────────────────────────

    /**
     * TSDialog.confirm(message, callback)
     * Replacement for window.confirm(). Async, calls callback(true/false).
     */
    TSDialog.confirm = function (message, callback, options) {
        _createQuickDialog(message, "confirm", callback, options);
    };

    /**
     * TSDialog.alert(message, callback)
     * Replacement for window.alert(). Calls callback() on close.
     */
    TSDialog.alert = function (message, callback) {
        _createQuickDialog(message, "alert", callback, null);
    };

    function _isDangerConfirm(message, options) {
        return !!(options && options.danger);
    }

    function _createQuickDialog(message, type, callback, options) {
        // Build DOM
        var overlay = createElement("div", OVERLAY_CLASS);
        var wrapper = createElement("div", DIALOG_CLASS + " ts-dialog-quick");
        var dangerConfirm = type === "confirm" && _isDangerConfirm(message, options);
        if (dangerConfirm) {
            wrapper.classList.add("ts-dialog-quick-danger");
        }
        var body = createElement("div", "ts-dialog-quick-body", wrapper);
        body.innerHTML = message;

        var footer = createElement("div", "ts-dialog-buttons", wrapper);

        function close(result) {
            overlay.classList.remove(VISIBLE_CLASS);
            wrapper.classList.remove(VISIBLE_CLASS);
            document.removeEventListener("keydown", escHandler);
            // Remove from DOM after transition
            setTimeout(function () {
                if (overlay.parentNode) overlay.parentNode.removeChild(overlay);
                if (wrapper.parentNode) wrapper.parentNode.removeChild(wrapper);
            }, 200);
            if (typeof callback === "function") callback(result);
        }

        if (type === "confirm") {
            var cancelBtn = createElement("button", "ts-dialog-btn", footer);
            cancelBtn.textContent = TSDialog.labels.cancel;
            cancelBtn.addEventListener("click", function () { close(false); });

            var okBtn = createElement("button", "ts-dialog-btn ts-dialog-btn-primary", footer);
            if (dangerConfirm) {
                okBtn.classList.add("ts-dialog-btn-danger");
            }
            okBtn.textContent = TSDialog.labels.ok;
            okBtn.addEventListener("click", function () { close(true); });
        } else {
            var okBtn2 = createElement("button", "ts-dialog-btn ts-dialog-btn-primary", footer);
            okBtn2.textContent = TSDialog.labels.ok;
            okBtn2.addEventListener("click", function () { close(); });
        }

        // Overlay click = cancel for confirm, close for alert
        overlay.addEventListener("click", function () {
            close(type === "confirm" ? false : undefined);
        });

        var escHandler = function (e) {
            if (e.key === "Escape" || e.keyCode === 27) {
                close(type === "confirm" ? false : undefined);
            }
        };

        document.body.appendChild(overlay);
        document.body.appendChild(wrapper);

        // Trigger show transition on next frame
        requestAnimationFrame(function () {
            overlay.classList.add(VISIBLE_CLASS);
            wrapper.classList.add(VISIBLE_CLASS);
            document.addEventListener("keydown", escHandler);
            // Focus OK button
            var focus = wrapper.querySelector(".ts-dialog-btn-primary");
            if (focus) focus.focus();
        });
    }

    // Localizable labels (set from JSP)
    TSDialog.labels = {
        ok: "OK",
        cancel: "Cancel"
    };

    // Expose globally
    window.TSDialog = TSDialog;

})(window);
