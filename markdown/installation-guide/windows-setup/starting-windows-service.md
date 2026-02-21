# Запуск TrackStudio как сервиса Windows

**Jetty (сервер приложений, поставляемый с TrackStudio SA)**

## Чтобы установить Jetty как сервис Windows:

```
>jettyService /install
Installed service 'jettyService'.
```

## Чтобы запустить TrackStudio как сервис:

```
>jettyService /start
Starting service 'jettyService'.
```

## Чтобы остановить сервис TrackStudio:

```
>jettyService /stop
Stopping service 'jettyService'.
Service stopped
```

## Чтобы деинсталлировать сервис TrackStudio:

```
>jettyService /uninstall
Uninstalled service 'jettyService'.
```
