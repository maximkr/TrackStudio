function search_attachment(term, _id, cellNr) {
    var suche = term.value.toLowerCase();
    var table = document.getElementById(_id);
    var ele;
    for (var r = 1; r < table.rows.length; r++){
        var show = false;
        for (var inx=0;inx!=cellNr.length;++inx) {
            ele = table.rows[r].cells[cellNr[inx]].innerHTML.replace(/<[^>]+>/g,"");
            if (!show) {
                if (ele.toLowerCase().indexOf(suche)>=0) {
                    show = true;
                    break;
                }
            }
        }
        if (show) {
            table.rows[r].style.display = '';
        } else {
            table.rows[r].style.display = 'none';
        }
    }
}