[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)

---

# Как собрать TrackStudio из исходного кода

Для того, чтобы собрать TrackStudio, вам понадобятся: [Java SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) и [Apache Ant](http://ant.apache.org/bindownload.cgi). Лучше, если у вас будет [какая-нибудь IDE](http://www.jetbrains.com/idea/download/), но для небольших изменений можно обойтись и простым **текстовым** редактором.

## Как скомпилировать проект без использования IDE

Убедитесь, что Ant установлен правильно. Для этого наберите в командной строке

```
ant
```

Ответ должен быть таким:

```
Buildfile: build.xml does not exist!
Build failed
```

В случае, если Ant установлен неправильно, [обратитесь к документации](http://ant.apache.org/manual/index.html).

1. Скачайте исходный код TrackStudio.
2. Разверните исходный код TrackStudio в любую пустую папку.
3. В командной строке наберите ant war.
4. Дождитесь завершения сборки.

![](../../images/compilation_9.png)

1. В случае удачной компиляции в папке **dist** должен появиться файл **TrackStudio.war**. Этот файл можно развернуть в директорию **webapps/TrackStudio** вашего **резервного экземпляра** TrackStudio.
2. После этого резервный экземпляр можно запустить и посмотреть, что получилось.

## Как создать и скомпилировать проект в IntelliJ IDEA.

1. Скачайте исходный код TrackStudio.
2. Разверните исходный код TrackStudio в любую пустую папку.
3. Запустите IDEA.
4. Создайте новый проект, укажите в качестве его корня папку, в которую вы развернули архив с исходниками.

![](../../images/compilation_1.png)

![](../../images/compilation_2.png)

![](../../images/compilation_3.png)

![](../../images/compilation_4.png)

![](../../images/compilation_5.png)

1. Перейдите в меню **File->Project Structure**.
2. Выберите вкладку **Libraries**.
3. Создайте библиотеку **TrackStudio**.
4. Добавьте туда все классы из папки **webapps/TrackStudio/WEB-INF/lib**.

![](../../images/compilation_6.png)

1. Затем откройте вкладку Ant Build (обычно она расположена справа на окне IDEA)
2. Выберите файл build.xml из папки, куда вы развернули исходные коды TrackStudio

![](../../images/compilation_7.png)

1. Запустите задачу war из списка заданий Ant Build
2. В случае удачной компиляции в папке **dist** должен появиться файл **TrackStudio.war**. Этот файл можно развернуть в директорию **webapps/TrackStudio** вашего **резервного экземпляра** TrackStudio.

![](../../images/compilation_8.png)

1. После этого резервный экземпляр можно запустить и посмотреть, что получилось.

Таким образом вы можете менять в TrackStudio практически всё, либо дорабатывать свои части.

## Как убрать индикацию синтаксических ошибок

![](../../images/compilation_10.png)

![](../../images/compilation_11.png)

Для того, чтобы в IDEA было удобно редактировать наш код, нужно ее познакомить поближе с особенностями нашего проекта.

Для начала нужно подключить библиотеку javax.servlet.jar. Она находится в папке **jetty/lib** развернутых исходников.

1. В IDEA перейдите в меню **File->Project Structure**.
2. Выберите пункт **Modules**.
3. Выберите модуль **TrackStudio**.
4. Выберите вкладку Dependencies.
5. Нажмите кнопку Add и выберите Single Module Library
6. Отыщите javax.servlet.jar и добавьте ее в проект

![](../../images/compilation_12.png)

После этого исходный код будет выглядеть так:

![](../../images/compilation_13.png)

Настала очередь библиотек с тегами.

Нам нужно создать Facet для нашего проекта. Для этого:

1. В IDEA перейдите в меню **File->Project Structure**.
2. Выберите пункт **Facets**.
3. Выберите **Web**.
4. Добавьте новый facet к модулю TrackStudio

![](../../images/compilation_15.png)

1. Укажите путь до файла **web.xml**: **webapps/TrackStudio/WEB-INF/web.xml** либо **etc/webxml/web.jspc20.xml**
2. Укажите путь до ресурсов (Web Resource Directory Path): **webapps/TrackStudio/**
3. Укажите там же Relative Path как **/**

![](../../images/compilation_17.png)

![](../../images/compilation_18.png)

## Отладка

Для отладки кода можно настроить Remote Debug. Для этого:

1. В IDEA перейдите в меню **Run->Edit Configuration**.
2. Создайте новую конфигурацию. Выберите пункт **Remote**.
3. Подключите там же файлы исходного кода из модуля TrackStudio.

![](../../images/compilation_19.png)

1. Скопируйте строчку в файл startJetty.vmoptions в корне вашего резервного экземпляра TrackStudio

```
-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
```

![](../../images/compilation_20.png)

1. Запустите jetty

```
sh startJetty
```

1. В терминале должна появиться строчка

```
Listening for transport dt_socket at address: 5005
```

1. Перейдите в IDEA.
2. Выберите ранее созданную конфигурацию 'TrackStudio и нажмите кнопку отладки.

![](../../images/compilation_21.png)

1. Поставьте в нужных вам местах точки останова.
2. Через браузер зайдите в TrackStudio и выполните действия, которые должны привести к останову отладчика в нужных вам точках.

![](../../images/compilation_22.png)

1. Найдите место, где происходит ошибка.
2. Исправьте ошибку.
3. Остановите резервный экземпляр TrackStudio.
4. Перекомпилируйте ваш исходный код по инструкции выше.
5. Снова приступите к отладке.

---

[Домой](../../index.md) | [Наверх (Как писать скрипты и триггеры в TrackStudio)](index.md)
