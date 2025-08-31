tinymce.init({
    // Селектор для текстовых областей с классом mceEditor
    selector: 'textarea.mceEditor',
    
    // Основные настройки
    content_css: contextPath + "/tinymce.css",
    convert_urls: false,
    
    // Плагины (совместимые с TinyMCE 5)
    plugins: 'autolink lists table save link anchor image preview media searchreplace fullscreen insertdatetime nonbreaking charmap directionality visualchars code visualblocks emoticons autoresize wordcount template paste',
    
    // Панель инструментов (упрощенная для TinyMCE 8)
    toolbar: [
        'undo redo | bold italic underline strikethrough | searchreplace',
        'alignleft aligncenter alignright alignjustify | bullist numlist | outdent indent',
        'link unlink anchor image | forecolor backcolor | fontsize',
        'table | charmap | customTemplates | preview fullscreen | code btnCode'
    ],
    
    // Настройки меню
    menubar: 'file edit view insert format tools table help',
    
    // Настройки высоты и изменения размера
    min_height: 300,
    resize: true,
    
    // Блоки и новые строки
    forced_root_block: false,
    
    // Проверка орфографии
    browser_spellcheck: true,
    
    // Настройки изображений
    image_advtab: true,
    
    // Настройки ссылок
    link_assume_external_targets: false,
    
    // Автосохранение (упрощенная конфигурация)
    autosave_interval: '30s',
    
    // Настройка пользовательских кнопок и функций
    setup: function(editor) {
        // Добавляем кастомную кнопку Code для TinyMCE 5 (правильный API)
        editor.ui.registry.addButton('btnCode', {
            text: 'Code',
            icon: 'sourcecode',
            tooltip: 'Insert Java Code Block',
            onAction: function() {
                editor.focus();
                var selectedText = editor.selection.getContent({format: 'text'});
                editor.selection.setContent('[code=java]' + selectedText + '[/code]');
            }
        });

        // Добавляем кнопку для шаблонов (замена template плагина)
        editor.ui.registry.addMenuButton('customTemplates', {
            text: 'Templates',
            icon: 'new-document',
            tooltip: 'Insert Template',
            fetch: function(callback) {
                var items = [
                    {type: 'menuitem', text: 'FullPathLink', onAction: function() { insertTemplate(editor, 'FullPathLink.html'); }},
                    {type: 'menuitem', text: 'SimpleNameLink', onAction: function() { insertTemplate(editor, 'SimpleNameLink.html'); }},
                    {type: 'separator'},
                    {type: 'menuitem', text: 'SubtaskFilter', onAction: function() { insertTemplate(editor, 'SubtaskFilter.html'); }},
                    {type: 'separator'},
                    {type: 'menuitem', text: 'UDFViewMacro', onAction: function() { insertTemplate(editor, 'UDFViewMacro.html'); }},
                    {type: 'separator'},
                    {type: 'menuitem', text: 'BurndownChart', onAction: function() { insertTemplate(editor, 'BurndownChart.html'); }},
                    {type: 'menuitem', text: 'PersonalActivityChart', onAction: function() { insertTemplate(editor, 'PersonalActivityChart.html'); }},
                    {type: 'menuitem', text: 'TeamActivityChart', onAction: function() { insertTemplate(editor, 'TeamActivityChart.html'); }},
                    {type: 'menuitem', text: 'TrendChart', onAction: function() { insertTemplate(editor, 'TrendChart.html'); }},
                    {type: 'menuitem', text: 'SpentTimeChart', onAction: function() { insertTemplate(editor, 'SpentTimeChart.html'); }},
                    {type: 'menuitem', text: 'WorkloadChart', onAction: function() { insertTemplate(editor, 'WorkloadChart.html'); }},
                    {type: 'menuitem', text: 'StateChart', onAction: function() { insertTemplate(editor, 'StateChart.html'); }},
                    {type: 'menuitem', text: 'ResolutionChart', onAction: function() { insertTemplate(editor, 'ResolutionChart.html'); }},
                    {type: 'menuitem', text: 'SyntaxHighlighter', onAction: function() { insertTemplate(editor, 'SyntaxHighlighter.html'); }}
                ];
                callback(items);
            }
        });
    }
});

// Функция для загрузки и вставки шаблонов
function insertTemplate(editor, templateFile) {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', contextPath + '/template_macros/' + templateFile, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                editor.insertContent(xhr.responseText);
            } else {
                console.error('Ошибка загрузки шаблона:', templateFile);
            }
        }
    };
    xhr.send();
}

var is_new = true;