(function (window, document) {
    "use strict";

    if (!window || !document || !window.TSAvatar) {
        return;
    }

    var TSAvatar = window.TSAvatar;
    var Enhancer = window.TSAvatarEnhancer || {};

    function hasTreeParent(node) {
        var current = node;
        while (current && current !== document.body) {
            if (current.id === "task_tree") {
                return true;
            }
            if (current.id === "user_tree") {
                return false;
            }
            current = current.parentNode;
        }
        return false;
    }

    Enhancer.isUserCandidate = function (node) {
        if (!node || node.nodeType !== 1 || node.className.indexOf("ts-user-with-avatar") !== -1) {
            return false;
        }
        if (node.closest && (node.closest(".ts-search-item__icons") || node.closest(".ts-search-item"))) {
            return false;
        }
        if (node.id && /-login$/.test(node.id)) {
            return false;
        }
        if (hasTreeParent(node)) {
            return false;
        }
        if (node.querySelector("img[src*='ico.status.gif']")) {
            return false;
        }
        if (node.querySelector("img[src*='arw.usr']")) {
            return true;
        }
        if (typeof node.className === "string" && /(^|\s)user(\s|$)/.test(node.className)) {
            return true;
        }
        var title = node.getAttribute("title") || "";
        if (title.indexOf("email:") !== -1) {
            return true;
        }
        if (node.id === "loggedUser" || node.className.indexOf("ts-activity-item__user") !== -1) {
            return true;
        }
        var href = node.getAttribute("href") || "";
        return node.tagName === "A" && href.indexOf("/user/") !== -1;
    };

    Enhancer.enhanceSingleUserNode = function (node) {
        if (!Enhancer.isUserCandidate(node)) {
            return;
        }
        var fullName = TSAvatar.normalizeText(node.textContent);
        if (!fullName) {
            var titleText = TSAvatar.normalizeText(node.getAttribute("title") || "");
            if (titleText) {
                var emailPos = titleText.toLowerCase().indexOf("email:");
                if (emailPos > -1) {
                    titleText = titleText.substring(0, emailPos);
                }
                fullName = TSAvatar.normalizeText(titleText);
            }
        }
        if (!fullName) {
            return;
        }

        var userIcon = node.querySelector("img[src*='arw.usr']");
        while (userIcon) {
            userIcon.parentNode.removeChild(userIcon);
            userIcon = node.querySelector("img[src*='arw.usr']");
        }

        var avatar = document.createElement("span");
        avatar.className = "ts-user-avatar";
        avatar.setAttribute("aria-hidden", "true");
        avatar.textContent = TSAvatar.getAvatarInitial(fullName);
        TSAvatar.applyPalette(node, fullName, "ts-user-avatar");
        node.className += " ts-user-with-avatar";
        node.insertBefore(avatar, node.firstChild);
    };

    Enhancer.enhanceTsUserNode = function (node) {
        if (!node || node.nodeType !== 1 || node.className.indexOf("ts-user--avatarized") !== -1) {
            return;
        }
        if (node.className.indexOf("ts-user--group") !== -1 || hasTreeParent(node)) {
            return;
        }
        var nameNode = node.querySelector(".ts-user__name");
        var fullName = TSAvatar.normalizeText(nameNode ? nameNode.textContent : node.textContent);
        if (!fullName) {
            return;
        }

        var avatarNode = node.querySelector(".ts-avatar");
        if (!avatarNode) {
            avatarNode = document.createElement("span");
            avatarNode.className = "ts-avatar";
            avatarNode.setAttribute("aria-hidden", "true");
            if (nameNode) {
                node.insertBefore(avatarNode, nameNode);
            } else {
                node.insertBefore(avatarNode, node.firstChild);
            }
        }
        avatarNode.textContent = TSAvatar.getAvatarInitial(fullName);
        TSAvatar.applyPalette(avatarNode, fullName, "ts-user-avatar");
        node.className += " ts-user--avatarized";
    };

    Enhancer.enhanceRoleNode = function (node) {
        if (!node || node.nodeType !== 1 || hasTreeParent(node)) {
            return;
        }
        if (typeof node.className !== "string" || node.className.indexOf("ts-role-with-avatar") !== -1) {
            return;
        }
        if (!node.querySelector("img[src*='ico.status.gif']")) {
            return;
        }
        var roleName = TSAvatar.normalizeText(node.textContent);
        if (!roleName) {
            return;
        }

        var roleBadge = document.createElement("span");
        roleBadge.className = "ts-role-avatar";
        roleBadge.setAttribute("aria-hidden", "true");
        roleBadge.textContent = TSAvatar.getAvatarInitial(roleName);
        TSAvatar.applyPalette(node, "role:" + roleName, "ts-role-avatar");
        node.className += " ts-role-with-avatar";

        var roleIcons = node.querySelectorAll("img[src*='ico.status.gif']");
        for (var i = 0; i < roleIcons.length; i++) {
            roleIcons[i].parentNode.removeChild(roleIcons[i]);
        }
        node.insertBefore(roleBadge, node.firstChild);
    };

    Enhancer.enhanceRoleAvatars = function (root) {
        if (!root || !root.querySelectorAll) {
            return;
        }
        var roleIcons = root.querySelectorAll("img[src*='ico.status.gif']");
        for (var i = 0; i < roleIcons.length; i++) {
            var parent = roleIcons[i].parentNode;
            if (parent && parent.nodeType === 1) {
                Enhancer.enhanceRoleNode(parent);
            }
        }
    };

    Enhancer.enhanceLegacyUserIconParents = function (root) {
        if (!root || !root.querySelectorAll) {
            return;
        }
        var userIcons = root.querySelectorAll("img[src*='arw.usr']");
        for (var i = 0; i < userIcons.length; i++) {
            var parent = userIcons[i].parentNode;
            if (parent && parent.nodeType === 1) {
                Enhancer.enhanceSingleUserNode(parent);
            }
        }
    };

    Enhancer.enhanceActivityAvatar = function (node) {
        if (!node || node.nodeType !== 1 || hasTreeParent(node)) {
            return;
        }
        if (node.className.indexOf("ts-activity-item__avatar--enhanced") !== -1) {
            return;
        }
        var fullName = TSAvatar.normalizeText(node.getAttribute("title") || "");
        if (!fullName) {
            fullName = TSAvatar.normalizeText(node.textContent);
        }
        if (!fullName) {
            fullName = node.id ? ("user:" + node.id) : "user:unknown";
        }
        var avatarText = TSAvatar.normalizeText(node.textContent);
        if (!avatarText || avatarText === "?" || !TSAvatar.isLetterOrDigit(avatarText.charAt(0))) {
            node.textContent = TSAvatar.getAvatarInitial(fullName);
        }
        TSAvatar.applyPalette(node, fullName, "ts-user-avatar");
        node.className += " ts-activity-item__avatar--enhanced";
    };

    Enhancer.enhanceUserAvatars = function (root) {
        if (!root || !root.querySelectorAll) {
            return;
        }
        Enhancer.enhanceLegacyUserIconParents(root);
        var legacyNodes = root.querySelectorAll("a.user, span.user, div.user, a.internal");
        for (var i = 0; i < legacyNodes.length; i++) {
            Enhancer.enhanceSingleUserNode(legacyNodes[i]);
        }
        var modernNodes = root.querySelectorAll(".ts-user");
        for (var j = 0; j < modernNodes.length; j++) {
            Enhancer.enhanceTsUserNode(modernNodes[j]);
        }
        Enhancer.enhanceRoleAvatars(root);
        var activityAvatars = root.querySelectorAll(".ts-activity-item__avatar");
        for (var k = 0; k < activityAvatars.length; k++) {
            Enhancer.enhanceActivityAvatar(activityAvatars[k]);
        }
    };

    Enhancer.init = function () {
        Enhancer.enhanceUserAvatars(document);
        if (typeof MutationObserver === "undefined" || !document.body) {
            return;
        }
        var observer = new MutationObserver(function (mutations) {
            for (var i = 0; i < mutations.length; i++) {
                var added = mutations[i].addedNodes;
                for (var j = 0; j < added.length; j++) {
                    var node = added[j];
                    if (node.nodeType !== 1) {
                        continue;
                    }
                    if (node.matches && node.matches("a.user, span.user, div.user, a.internal")) {
                        Enhancer.enhanceSingleUserNode(node);
                    }
                    if (node.matches && node.matches("img[src*='arw.usr']")) {
                        Enhancer.enhanceSingleUserNode(node.parentNode);
                    }
                    if (node.matches && node.matches(".ts-activity-item__avatar")) {
                        Enhancer.enhanceActivityAvatar(node);
                    }
                    if (node.matches && node.matches("img[src*='ico.status.gif']")) {
                        Enhancer.enhanceRoleNode(node.parentNode);
                    }
                    Enhancer.enhanceUserAvatars(node);
                }
            }
        });
        observer.observe(document.body, {childList: true, subtree: true});
    };

    Enhancer.enhanceTreeUserNode = function (span) {
        if (!span) {
            return;
        }
        var titleNode = span.querySelector("span.fancytree-title");
        if (!titleNode) {
            return;
        }

        var rawName = TSAvatar.normalizeText(titleNode.textContent);
        var userName = rawName.replace(/\s+\(\d+\)\s*$/, "");
        if (!userName) {
            userName = rawName;
        }
        if (!userName) {
            return;
        }

        var avatarNode = span.querySelector("span.ts-tree-user-avatar");
        if (!avatarNode) {
            avatarNode = document.createElement("span");
            avatarNode.className = "ts-tree-user-avatar";
            avatarNode.setAttribute("aria-hidden", "true");
            span.insertBefore(avatarNode, titleNode);
        }

        var legacyIcons = span.querySelectorAll("span.fancytree-custom-icon, span.fancytree-icon, img.fancytree-icon");
        for (var i = 0; i < legacyIcons.length; i++) {
            legacyIcons[i].parentNode.removeChild(legacyIcons[i]);
        }

        TSAvatar.applyPalette(avatarNode, "tree:" + userName, "ts-tree-user-avatar");
        avatarNode.textContent = TSAvatar.getAvatarInitial(userName);
    };

    Enhancer.enhanceUserTreeIcons = function (selector) {
        if (!window.jQuery) {
            return;
        }
        var tree = window.jQuery(selector).fancytree("getTree");
        if (!tree) {
            return;
        }
        tree.visit(function (node) {
            Enhancer.enhanceTreeUserNode(node.span);
            return true;
        });
    };

    window.TSAvatarEnhancer = Enhancer;
})(window, document);
