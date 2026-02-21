[Домой](../index.md) | [Наверх (Руководство администратора)](index.md)

---

# Как скрыть фрейм с деревом задач

Найдите в вашем экземпляре TrackStudio папку **webapps/TrackStudio**, а в ней файл **staticframeset.html**

В этом файле отредактируйте секцию <frameset> вот так:

```
<frameset id="fsid" cols="*">
<!-- <frame  id="treeFrame" src="jsp/TreeFrame.jsp" name="treeFrame"> -->
<frame  id="mainFrame" src="jsp/TSFrame.jsp" name="mainFrame">
</frameset>
```

Там же можно изменить ширину колонки с деревом, например, увеличить:

```
<frameset id="fsid" cols="480, *">
```

Или уменьшить

```
<frameset id="fsid" cols="120, *">
```

---

[Домой](../index.md) | [Наверх (Руководство администратора)](index.md)
