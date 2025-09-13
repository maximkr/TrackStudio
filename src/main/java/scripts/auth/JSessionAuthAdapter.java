package scripts.auth;

import com.trackstudio.app.adapter.AuthAdapter;
import com.trackstudio.exception.GranException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Класс авторизации
 */
public class JSessionAuthAdapter implements AuthAdapter {

    private static Log log = LogFactory.getLog(JSessionAuthAdapter.class);

    /**
     * Инициализирует адаптер
     *
     * @return TRUE - инициализация прошла успешно, FALSE - нет
     */
    public boolean init() {
        return true;
    }

    /**
     * Возвращает текстовое описание адаптера
     *
     * @return adapter's description
     */
    public String getDescription() {
        return "Example Authentication Adapter";
    }

    /**
     * Производит авторизацию
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @param result   Результат авторизации
     * @return TRUE - если авторизация прошла удачно, FALSE - если нет
     * @throws com.trackstudio.exception.GranException при необходимости
     */
    public boolean authorizeImpl(String userId, String password, boolean result, HttpServletRequest request) throws GranException {
        log.info("Authorize example");
        //if we already authenticated or josso properties undefined - skip all
        if (result) {
            return result;
        }
        return false;
    }

    /**
     * Меняет пароль пользователя. Реализация не нужна
     *
     * @param userId   ID пользователя
     * @param password пароль
     * @throws com.trackstudio.exception.GranException при необходимости
     */
    public void changePasswordImpl(String userId, String password) throws GranException {
        log.info("Change password example");
    }
}
