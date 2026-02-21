[Домой](../../../index.md) | [Наверх (Как быстро и просто сделать свой интерфейс для TrackStudio)](index.md)

---

# Как организовать переход по ссылкам внутри шаблона

Ссылка в шаблонах строятся по формату:

```
${contextPath}/template/${template}/task/${task.number}
```

Где **${contextPath}** - контекст (переменная уже задана), а **${template}** - название шаблона (переменная template уже содержит его).

Для вывода списка подзадач текущей задачи, отфильтрованных фильтром с названием **${filter}**:

```
${contextPath}/template/${template}/task/${task.number}/filter/${filter}
```

Для ссылки на вложение **${attachment}** к текущей задаче:

```
${contextPath}/download/task/${task.number}/${attachment.id}/${attachment.name}
```

А предпросмотр приложенной картинки можно сделать, например, так:

```

<img align="middle" border="0"
src="${contextPath}/TSImageServlet?session=${task.secure.id}&attId=${attachment.id}&width=100&height=75"/>
```

---

[Домой](../../../index.md) | [Наверх (Как быстро и просто сделать свой интерфейс для TrackStudio)](index.md)
