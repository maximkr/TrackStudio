[Home](../index.md) | [Up (Administrator's Guide)](index.md)

---

# How to Localize TrackStudio Interface

This article describes how simple it is to localize TrackStudio interface independently.

As of today TrackStudio comes with three localization variants: Russian, English and Ukrainian languages. The main language for all the texts is Russian (it was English in versions till 4.0). Examples of data bases are also shipped in localized versions; you can select the required language for creating the data bases. This article is intended for those, who are planning to translate the interface of TrackStudio into one of the languages not supported as of today. Other users may also use this article for the purpose of changing the terms used in TrackStudio into the ones used in their departments.

As a matter of fact, localization is not only the text we see on the screen, but it also includes formats for dates and time. TrackStudio uses standard localization mechanism if Java, based on *MessageFormat* and *NumberFormat*. For each user, different locales and time zones can be assigned; these details are used for displaying all the dates and time, in web-interface as well as in email notifications.

Different languages may need different encodings. We recommend using international encoding UTF-8, but if desired you can also use Windows-1251 and Latin-1. Encoding is specified for the entire package of TrackStudio. You can do this with the help of program “**Server Manager**” (**SMAN**), or you can set the encoding in the file trackstudio.properties.

## language-files

Entire text, displayed in TrackStudio, is stored in files **etc/language/language_xx.properties**, where xx – two letter abbreviation as per ISO-639 (ru, en, uk). In these files, text is stored in the form of pairs key-meaning.

For the purpose of translating the text of TrackStudio into another language, copy the original file to the new language_xx.properties, corresponding to your language. Then translate the contents of file (names of keys, of course, need not be translated). The final file must be in the format ASCII, therefore, if you need to use special characters (e.g. Russian language letter), you will need to convert the file with the help of utility native2ascii, included in JDK.

```
native2ascii -encoding XXX my_translation.properties language_YY.properties
```

If you don’t require translating TrackStudio into another language, but you need, for example, to amend some strings or terms, you can instead of editing the file **language_xx.properties** create a new **language_xx1.properties** (e.g. **language_en1.properties**), in which you need to specify only the meanings of redefined keys. In that case, TrackStudio will first search for the key in this file **language_en1.properties **. If there is no such key available, then it will take from **language_en.properties**. If for this key there is no meaning in any of the required files, the user will see the key itself.

Final files must be saved in the folder **webapps/TrackStudio/WEB-INF/classes**

Set your trackstudio.defaultLocale option from **trackstudio.properties** with your locale

```
trackstudio.defaultLocale en1
```

Choose your locale in user settings.

![](../images/LocaleEn.PNG)

---

[Home](../index.md) | [Up (Administrator's Guide)](index.md)
