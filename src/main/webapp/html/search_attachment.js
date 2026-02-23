function search_attachment(term, _id, cellNr) {
    var query = (term && term.value ? term.value : "").toLowerCase();
    var table = document.getElementById(_id);
    if (!table || !table.rows) {
        return;
    }

    var hasColumns = Array.isArray(cellNr) && cellNr.length > 0;
    for (var r = 1; r < table.rows.length; r++) {
        var row = table.rows[r];
        var show = query.length === 0;

        if (!show) {
            if (hasColumns) {
                for (var inx = 0; inx < cellNr.length; inx++) {
                    var columnIndex = cellNr[inx];
                    if (!row.cells || !row.cells[columnIndex]) {
                        continue;
                    }
                    var cellText = (row.cells[columnIndex].textContent || "").toLowerCase();
                    if (cellText.indexOf(query) >= 0) {
                        show = true;
                        break;
                    }
                }
            } else {
                var rowText = (row.textContent || "").toLowerCase();
                show = rowText.indexOf(query) >= 0;
            }
        }

        row.style.display = show ? "" : "none";
    }
}
