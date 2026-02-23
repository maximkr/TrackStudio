[Home](../../index.md) | [Up (Developer's Guide)](../index.md)

---

# How to Make the Value of Custom Field Computable

Values of the custom fields in TrackStudio can not only be set manually, but they can be computed automatically. They can either be computed every time, when the value is displayed, or cached and computed only in case of change in the task properties.

Values of task custom fields can be calculated with the help of scripts, and particularly the scripts, corresponding to the interface

**com.trackstudio.external.TaskUDFValueScript**.

There are a total of 10 types of custom fields in TrackStudio:

| String | String |  |
| --- | --- | --- |
| Whole number | Integer |  |
| Date | Calendar |  |
| List | com.trackstudio.tools.Pair | Contained in trackstudio.jar. Constructor Pair(String key, String value) |
| Fraction | Double |  |
| Text | String |  |
| Multiple List | List<Pair> | Contained in trackstudio.jar |
| Task | List<String> | List of task id |
| User | List<String> | List of user id |
| URL | com.trackstudio.containers.Link | Contained in trackstudio.jar. Constructor Link(String link, String description) |

## Examples of Scripts

## [Integer](integer/index.md)

---

[Home](../../index.md) | [Up (Developer's Guide)](../index.md)
