/**
 * TSWorkflowDialog — Modal dialog for workflow operations.
 *
 * Features:
 *   - Opens as modal overlay on current page (no navigation)
 *   - Quick-action mode for simple operations
 *   - Full form mode for complex operations
 *   - Keyboard shortcuts: Ctrl+Enter (submit), Esc (cancel)
 *   - Draft auto-save to localStorage
 *   - Shows what changed before saving
 *
 * Usage:
 *   TSWorkflowDialog.open({
 *     taskId: '123',
 *     mstatusId: '456',
 *     operationName: 'Close',
 *     contextPath: '/TrackStudio',
 *     formUrl: '/MessageCreateAction.do?method=page&id=123&mstatus=456',
 *     submitUrl: '/MessageCreateAction.do?method=save',
 *     onComplete: function(result) { ... }
 *   });
 */

(function (window, document) {
    "use strict";

    var TSWorkflowDialog = {};

    // Configuration
    var OVERLAY_CLASS = "ts-workflow-overlay";
    var DIALOG_CLASS = "ts-workflow-dialog";
    var VISIBLE_CLASS = "visible";
    var DIALOG_WIDTH = 640;
    var DIALOG_MAX_HEIGHT = 0.85; // 85% of viewport

    // State
    var currentDialog = null;
    var draftKey = null;
    var draftTimer = null;
    var checkInterval = null;

    // ── Helper Functions ─────────────────────────────────────

    function createElement(tag, className, parent) {
        var el = document.createElement(tag);
        if (className) el.className = className;
        if (parent) parent.appendChild(el);
        return el;
    }

    function getScrollbarWidth() {
        var outer = document.createElement("div");
        outer.style.visibility = "hidden";
        outer.style.overflow = "scroll";
        outer.style.width = "100px";
        document.body.appendChild(outer);
        var scrollbarWidth = outer.offsetWidth - outer.clientWidth;
        document.body.removeChild(outer);
        return scrollbarWidth;
    }

    function lockBodyScroll() {
        var scrollbarWidth = getScrollbarWidth();
        document.body.style.overflow = "hidden";
        document.body.style.paddingRight = scrollbarWidth + "px";
    }

    function unlockBodyScroll() {
        document.body.style.overflow = "";
        document.body.style.paddingRight = "";
    }

    function escapeHtml(str) {
        if (!str) return "";
        return String(str)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    // ── Draft Management ─────────────────────────────────────

    function saveDraft(content) {
        if (!draftKey || !content) return;
        try {
            localStorage.setItem(draftKey, JSON.stringify({
                content: content,
                timestamp: Date.now()
            }));
        } catch (e) {
            // localStorage not available
        }
    }

    function loadDraft() {
        if (!draftKey) return null;
        try {
            var data = localStorage.getItem(draftKey);
            if (data) {
                var parsed = JSON.parse(data);
                // Expire drafts after 24 hours
                if (Date.now() - parsed.timestamp < 24 * 60 * 60 * 1000) {
                    return parsed.content;
                }
                localStorage.removeItem(draftKey);
            }
        } catch (e) {
            // localStorage not available
        }
        return null;
    }

    function clearDraft() {
        if (!draftKey) return;
        try {
            localStorage.removeItem(draftKey);
        } catch (e) {}
    }

    function startDraftAutosave(textarea) {
        stopDraftAutosave();
        draftTimer = setInterval(function () {
            if (textarea && textarea.value) {
                saveDraft(textarea.value);
            }
        }, 5000);
    }

    function stopDraftAutosave() {
        if (draftTimer) {
            clearInterval(draftTimer);
            draftTimer = null;
        }
    }

    // ── Dialog Structure ─────────────────────────────────────

    function buildDialog(config) {
        var overlay = createElement("div", OVERLAY_CLASS);
        var dialog = createElement("div", DIALOG_CLASS);
        dialog.style.width = (config.width || DIALOG_WIDTH) + "px";
        dialog.style.maxHeight = (window.innerHeight * DIALOG_MAX_HEIGHT) + "px";

        // Header
        var header = createElement("div", "ts-workflow-dialog__header", dialog);
        var titleEl = createElement("h3", "ts-workflow-dialog__title", header);
        titleEl.textContent = config.operationName || "Execute Operation";
        var closeBtn = createElement("button", "ts-workflow-dialog__close", header);
        closeBtn.innerHTML = "&times;";
        closeBtn.setAttribute("aria-label", "Close");
        closeBtn.setAttribute("type", "button");

        // Body (will contain iframe or form)
        var body = createElement("div", "ts-workflow-dialog__body", dialog);

        // Footer
        var footer = createElement("div", "ts-workflow-dialog__footer", dialog);
        var cancelBtn = createElement("button", "ts-workflow-dialog__btn ts-workflow-dialog__btn--secondary", footer);
        cancelBtn.textContent = TSWorkflowDialog.labels.cancel;
        cancelBtn.setAttribute("type", "button");
        var submitBtn = createElement("button", "ts-workflow-dialog__btn ts-workflow-dialog__btn--primary", footer);
        submitBtn.textContent = config.submitLabel || TSWorkflowDialog.labels.submit;
        submitBtn.setAttribute("type", "button");

        // Loading indicator
        var loading = createElement("div", "ts-workflow-dialog__loading", body);
        loading.innerHTML = '<span class="ts-workflow-dialog__spinner"></span><span>' + 
            (TSWorkflowDialog.labels.loading || "Loading...") + '</span>';

        return {
            overlay: overlay,
            dialog: dialog,
            header: header,
            body: body,
            footer: footer,
            closeBtn: closeBtn,
            cancelBtn: cancelBtn,
            submitBtn: submitBtn,
            loading: loading
        };
    }

    // ── Form Loading ─────────────────────────────────────────

    function loadFormIframe(elements, config) {
        var iframe = createElement("iframe", "ts-workflow-dialog__iframe", elements.body);
        iframe.setAttribute("frameborder", "0");
        iframe.setAttribute("scrolling", "auto");
        iframe.setAttribute("title", "Operation form");
        iframe.setAttribute("src", config.formUrl);

        var submitHandler = null;

        iframe.onload = function () {
            try {
                var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
                
                // Hide unnecessary elements
                var hideSelectors = [
                    "#servicePanel", ".taskDescription", ".allrights",
                    ".ts-header", ".ts-main-tabs", ".ts-app-shell"
                ];
                hideSelectors.forEach(function (sel) {
                    var el = iframeDoc.querySelector(sel);
                    if (el) el.style.display = "none";
                });

                // Style the form
                var form = iframeDoc.querySelector("form");
                if (form) {
                    form.classList.add("ts-workflow-form");
                }

                // Remove loading indicator
                if (elements.loading && elements.loading.parentNode) {
                    elements.loading.parentNode.removeChild(elements.loading);
                }

                // Focus first input
                var firstInput = iframeDoc.querySelector('input[type="text"], textarea, select');
                if (firstInput) firstInput.focus();

                // Setup draft autosave for comment field
                var bugnote = iframeDoc.querySelector("#bugnote, textarea[name='bugnote']");
                if (bugnote) {
                    var draft = loadDraft();
                    if (draft && !bugnote.value) {
                        bugnote.value = draft;
                    }
                    startDraftAutosave(bugnote);
                }

                // Enable buttons
                enableButtons(iframeDoc);

                // Setup submit handler
                submitHandler = function (e) {
                    if (e) e.preventDefault();
                    submitForm(iframe, config);
                };
                
                form.addEventListener("submit", submitHandler);

            } catch (e) {
                console.error("Error accessing iframe content:", e);
            }
        };

        return submitHandler;
    }

    function enableButtons(iframeDoc) {
        // Enable disabled buttons (form initially disables them)
        var buttons = iframeDoc.querySelectorAll('button[disabled], input[disabled]');
        buttons.forEach(function (btn) {
            if (btn.name !== "cancelButton" || btn.type !== "button") {
                btn.disabled = false;
            }
        });
    }

    function submitForm(iframe, config) {
        try {
            var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
            var form = iframeDoc.querySelector("form");
            
            if (!form) {
                console.error("Form not found in iframe");
                return;
            }

            // Clear draft on successful submit
            clearDraft();
            stopDraftAutosave();

            // Add hidden field to indicate modal submission
            var modalInput = iframeDoc.createElement("input");
            modalInput.type = "hidden";
            modalInput.name = "modalSubmit";
            modalInput.value = "true";
            form.appendChild(modalInput);

            // Show loading state BEFORE submit to prevent race condition
            if (currentDialog && currentDialog.elements) {
                currentDialog.elements.dialog.classList.add("ts-workflow-dialog--submitting");
                currentDialog.elements.submitBtn.textContent = TSWorkflowDialog.labels.saving || "Saving...";
                currentDialog.elements.submitBtn.disabled = true;
            }

            // Submit the form
            form.submit();

            // Wait for response
            waitForResponse(iframe, config);

        } catch (e) {
            console.error("Error submitting form:", e);
        }
    }

    function waitForResponse(iframe, config) {
        var checkCount = 0;
        var maxChecks = 100; // 10 seconds max

        // N7: Store interval reference for cleanup
        checkInterval = setInterval(function () {
            checkCount++;
            try {
                var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
                var iframeLoc = iframe.contentWindow.location;

                // Check if we've navigated away from the form
                var hasError = iframeDoc.querySelector(".error");
                var hasSuccess = iframeDoc.querySelector(".ts-message-success");
                var hasNavigatedAway = iframeLoc.href.indexOf("MessageCreateAction") === -1;
                
                if (hasNavigatedAway || hasSuccess || hasError) {
                    
                    clearInterval(checkInterval);
                    
                    // Success - close dialog and refresh
                    if (typeof config.onComplete === "function") {
                        config.onComplete({ success: true });
                    }
                    
                    TSWorkflowDialog.close();
                    
                    // Refresh the parent page to show changes
                    if (window.parent && window.parent.TS && window.parent.TS.tree) {
                        window.parent.TS.tree.reload();
                    }
                    if (window.parent && window.parent.location) {
                        window.parent.location.reload();
                    }
                }
            } catch (e) {
                // Cross-origin or navigation happened
                clearInterval(checkInterval);
                if (typeof config.onComplete === "function") {
                    config.onComplete({ success: true });
                }
                TSWorkflowDialog.close();
            }

            if (checkCount >= maxChecks) {
                clearInterval(checkInterval);
                // P14: Handle timeout - re-enable submit button so user can retry
                if (currentDialog && currentDialog.elements) {
                    currentDialog.elements.submitBtn.disabled = false;
                    currentDialog.elements.submitBtn.textContent = TSWorkflowDialog.labels.submit || "Submit";
                    currentDialog.elements.dialog.classList.remove("ts-workflow-dialog--submitting");
                }
                // Show timeout error
                if (window.TSToast) {
                    TSToast.error(TSWorkflowDialog.labels.timeout || "Request timed out. Please try again.");
                }
            }
        }, 100);
    }

    // ── Quick Action Mode ────────────────────────────────────

    function buildQuickAction(config) {
        var elements = buildDialog(config);
        elements.dialog.classList.add("ts-workflow-dialog--quick");

        // Simple form
        var form = createElement("form", "ts-workflow-quick-form", elements.body);
        form.innerHTML = [
            '<div class="ts-workflow-quick__comment">',
            '  <label class="ts-workflow-quick__label">' + escapeHtml(TSWorkflowDialog.labels.comment || "Comment (optional)") + '</label>',
            '  <textarea name="bugnote" class="ts-workflow-quick__textarea" rows="3" placeholder="' + 
                escapeHtml(TSWorkflowDialog.labels.commentPlaceholder || "Add a comment...") + '"></textarea>',
            '</div>',
            '<input type="hidden" name="id" value="' + escapeHtml(config.taskId) + '">',
            '<input type="hidden" name="mstatus" value="' + escapeHtml(config.mstatusId) + '">',
            '<input type="hidden" name="method" value="save">',
            '<input type="hidden" name="session" value="' + escapeHtml(config.sessionId || '') + '">'
        ].join("");

        // Remove loading indicator
        if (elements.loading && elements.loading.parentNode) {
            elements.loading.parentNode.removeChild(elements.loading);
        }

        return elements;
    }

    // ── Event Handlers ───────────────────────────────────────

    function setupEventHandlers(elements, config) {
        var submitHandler = null;

        // Close button
        elements.closeBtn.addEventListener("click", function () {
            TSWorkflowDialog.close();
        });

        // Cancel button
        elements.cancelBtn.addEventListener("click", function () {
            TSWorkflowDialog.close();
        });

        // Overlay click
        elements.overlay.addEventListener("click", function (e) {
            if (e.target === elements.overlay) {
                TSWorkflowDialog.close();
            }
        });

        // Submit button
        elements.submitBtn.addEventListener("click", function () {
            if (config.quickMode) {
                submitQuickAction(elements, config);
            } else {
                var iframe = elements.body.querySelector("iframe");
                if (iframe) {
                    submitForm(iframe, config);
                }
            }
        });

        // Keyboard shortcuts
        var keyHandler = function (e) {
            if (e.key === "Escape" || e.keyCode === 27) {
                TSWorkflowDialog.close();
            }
            if ((e.ctrlKey || e.metaKey) && (e.key === "Enter" || e.keyCode === 13)) {
                e.preventDefault();
                elements.submitBtn.click();
            }
        };
        document.addEventListener("keydown", keyHandler);

        return {
            keyHandler: keyHandler,
            submitHandler: submitHandler
        };
    }

    function submitQuickAction(elements, config) {
        var form = elements.body.querySelector("form");
        if (!form) return;

        var formData = new FormData(form);
        formData.append("modalSubmit", "true");

        elements.submitBtn.textContent = TSWorkflowDialog.labels.saving || "Saving...";
        elements.submitBtn.disabled = true;

        fetch(config.submitUrl || config.contextPath + "/MessageCreateAction.do?method=save", {
            method: "POST",
            body: formData,
            credentials: "same-origin"
        })
        .then(function (response) {
            if (response.ok) {
                clearDraft();
                if (typeof config.onComplete === "function") {
                    config.onComplete({ success: true });
                }
                TSWorkflowDialog.close();
                // Refresh page
                if (window.location) {
                    window.location.reload();
                }
            } else {
                throw new Error("Server error: " + response.status);
            }
        })
        .catch(function (error) {
            console.error("Submit error:", error);
            elements.submitBtn.textContent = config.submitLabel || TSWorkflowDialog.labels.submit;
            elements.submitBtn.disabled = false;
            // Show error using TSToast (TSDialog may not be available)
            if (window.TSToast) {
                TSToast.error(TSWorkflowDialog.labels.error || "An error occurred. Please try again.");
            } else {
                alert(TSWorkflowDialog.labels.error || "An error occurred. Please try again.");
            }
        });
    }

    // ── Public API ───────────────────────────────────────────

    /**
     * Open workflow dialog
     * @param {Object} config - Dialog configuration
     * @param {string} config.taskId - Task ID
     * @param {string} config.mstatusId - Mstatus ID (operation)
     * @param {string} config.operationName - Operation name for title
     * @param {string} config.contextPath - Application context path
     * @param {string} config.formUrl - URL to load form (optional, uses iframe mode)
     * @param {string} config.submitUrl - URL to submit form
     * @param {boolean} config.quickMode - Use quick action mode (simple comment only)
     * @param {Function} config.onComplete - Callback when operation completes
     */
    TSWorkflowDialog.open = function (config) {
        // Close any existing dialog
        if (currentDialog) {
            TSWorkflowDialog.close();
        }

        // Setup draft key
        draftKey = "ts-workflow-draft-" + config.taskId + "-" + config.mstatusId;

        // Build dialog
        var elements;
        if (config.quickMode) {
            elements = buildQuickAction(config);
        } else {
            elements = buildDialog(config);
        }

        // Setup event handlers
        var handlers = setupEventHandlers(elements, config);

        // Add to DOM
        document.body.appendChild(elements.overlay);
        document.body.appendChild(elements.dialog);
        lockBodyScroll();

        // Store reference
        currentDialog = {
            elements: elements,
            handlers: handlers,
            config: config
        };

        // Load form (iframe mode)
        if (!config.quickMode && config.formUrl) {
            loadFormIframe(elements, config);
        }

        // Show with animation
        requestAnimationFrame(function () {
            elements.overlay.classList.add(VISIBLE_CLASS);
            elements.dialog.classList.add(VISIBLE_CLASS);

            // Focus submit button or first input
            setTimeout(function () {
                if (config.quickMode) {
                    var textarea = elements.dialog.querySelector("textarea");
                    if (textarea) textarea.focus();
                } else {
                    elements.submitBtn.focus();
                }
            }, 100);
        });

        return currentDialog;
    };

    /**
     * Close workflow dialog
     */
    TSWorkflowDialog.close = function () {
        if (!currentDialog) {
            // P13: Clean up any lingering handlers from race condition
            if (checkInterval) {
                clearInterval(checkInterval);
                checkInterval = null;
            }
            return;
        }

        stopDraftAutosave();

        // N7: Clear response check interval
        if (checkInterval) {
            clearInterval(checkInterval);
            checkInterval = null;
        }

        var elements = currentDialog.elements;
        var handlers = currentDialog.handlers;

        // Remove keyboard handler
        if (handlers && handlers.keyHandler) {
            document.removeEventListener("keydown", handlers.keyHandler);
        }

        // Hide with animation
        elements.overlay.classList.remove(VISIBLE_CLASS);
        elements.dialog.classList.remove(VISIBLE_CLASS);

        // Remove from DOM after transition
        setTimeout(function () {
            if (elements.overlay.parentNode) {
                elements.overlay.parentNode.removeChild(elements.overlay);
            }
            if (elements.dialog.parentNode) {
                elements.dialog.parentNode.removeChild(elements.dialog);
            }
            unlockBodyScroll();
        }, 200);

        currentDialog = null;
        draftKey = null;
    };

    /**
     * Check if dialog is open
     */
    TSWorkflowDialog.isOpen = function () {
        return currentDialog !== null;
    };

    // ── Localizable Labels ───────────────────────────────────
    TSWorkflowDialog.labels = {
        cancel: "Cancel",
        submit: "Execute",
        saving: "Saving...",
        loading: "Loading...",
        comment: "Comment (optional)",
        commentPlaceholder: "Add a comment...",
        error: "An error occurred. Please try again."
    };

    // Expose globally
    window.TSWorkflowDialog = TSWorkflowDialog;

})(window, document);
