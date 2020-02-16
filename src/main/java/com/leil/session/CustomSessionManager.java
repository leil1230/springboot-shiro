package com.leil.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

public class CustomSessionManager extends DefaultWebSessionManager {

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = this.getSessionId(sessionKey);
        if (sessionId == null) {
            return null;
        }
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            WebSessionKey webSessionKey = (WebSessionKey)sessionKey;
            request = webSessionKey.getServletRequest();
        }
        // 将session保存到request请求中，不用每次都去redis中取session信息
        if (request != null) {
            Session session = (Session) request.getAttribute(sessionId.toString());
            if (session == null) {
                // 调用父类的此方法从redis中读取session
                session = super.retrieveSession(sessionKey);
                // 将session存储到request中
                request.setAttribute(sessionId.toString(), session);
            }
            return session;
        }
        return super.retrieveSession(sessionKey);
    }
}
