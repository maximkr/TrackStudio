// Резервная копия конфигурации TinyMCE
tinyMCE.init({
    custom_shortcuts: false,
    content_css: contextPath + "/tinymce.css",
    gecko_spellcheck: true,
    convert_urls: false,
    mode: "textareas",
    theme: "advanced",
    editor_selector: "mceEditor",
    plugins: "autolink,lists,spellchecker,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",
    forced_root_block: false,
    force_br_newlines: false,
    force_p_newlines: false,
    theme_advanced_buttons1: "bold,italic,underline,strikethrough,|,cut,copy,pastetext,pasteword,|,undo,redo,|,search,replace,|,justifyleft,justifycenter,justifyright,justifyfull,bullist,numlist,|,outdent,indent,|,link,unlink,anchor,image,code",
    theme_advanced_buttons2: "forecolor,backcolor,|,fontselect,fontsizeselect,|,tablecontrols,|,charmap,iespell,|,print,fullscreen,|,template,btnCode,",
    theme_advanced_buttons3: "",
    theme_advanced_toolbar_location: "top",
    theme_advanced_toolbar_align: "left",
    theme_advanced_statusbar_location: "bottom",
    theme_advanced_resizing: true,
    setup: function(a) {
        a.addButton("btnCode", {
            title: "Code",
            image: "cssimages/code.png",
            onclick: function() {
                a.focus();
                var b = a.selection.getContent({format: "text"});
                a.selection.setContent("[code=java]" + b + "[/code]")
            }
        })
    },
    skin: "o2k7",
    skin_variant: "silver",
    template_external_list_url: "js/template_list.js",
    external_link_list_url: "js/link_list.js",
    external_image_list_url: "js/image_list.js",
    media_external_list_url: "js/media_list.js",
    template_cdate_classes: "cdate creationdate",
    template_mdate_classes: "mdate modifieddate",
    template_selected_content_classes: "selcontent",
    template_cdate_format: "%m/%d/%Y : %H:%M:%S",
    template_mdate_format: "%m/%d/%Y : %H:%M:%S",
    template_replace_values: {
        username: "Jack Black",
        staffid: "991234"
    },
    template_templates: [
        {title: "--------"},
        {title: "FullPathLink", src: "template_macros/FullPathLink.html", description: "It converts tasks number to a link and replaces tasks name to full link"},
        {title: "SimpleNameLink", src: "template_macros/SimpleNameLink.html", description: "It converts tasks number to a link"},
        {title: "--------"},
        {title: "SubtaskFilter", src: "template_macros/SubtaskFilter.html", description: "It converts tasks number to a table which has list of subtasks by filter"},
        {title: "--------"},
        {title: "UDFViewMacro", src: "template_macros/UDFViewMacro.html", description: "It converts tasks number to a view of udf"},
        {title: "--------"},
        {title: "BurndownChart", src: "template_macros/BurndownChart.html", description: "It converts tasks number to a Burndown Chat"},
        {title: "PersonalActivityChart", src: "template_macros/PersonalActivityChart.html", description: "It converts tasks number to a PersonalActivity Chart"},
        {title: "TeamActivityChart", src: "template_macros/TeamActivityChart.html", description: "It converts tasks number to a TeamActivity Chart"},
        {title: "TrendChart", src: "template_macros/TrendChart.html", description: "It converts tasks number to a Trend Chart"},
        {title: "SpentTimeChart", src: "template_macros/SpentTimeChart.html", description: "It converts tasks number to a SpentTime Chart"},
        {title: "WorkloadChart", src: "template_macros/WorkloadChart.html", description: "It converts tasks number to a Workload Chart"},
        {title: "StateChart", src: "template_macros/StateChart.html", description: "It converts tasks number to a State Chart"},
        {title: "ResolutionChart", src: "template_macros/ResolutionChart.html", description: "It converts tasks number to a Resolution Chart"},
        {title: "SyntaxHighlighter", src: "template_macros/SyntaxHighlighter.html", description: "It highlights the code"}
    ]
});

var is_new = true;
