package com.fsck.k9.backend.imap;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.fsck.k9.mail.AuthType;
import com.fsck.k9.mail.ServerSettings;
import com.fsck.k9.mail.store.imap.ImapStoreSettings;

import static com.fsck.k9.mail.helper.UrlEncodingHelper.encodeUtf8;


public class ImapStoreUriCreator {
    /**
     * Creates an ImapStore URI with the supplied settings.
     *
     * @param server
     *         The {@link ServerSettings} object that holds the server settings.
     *
     * @return An ImapStore URI that holds the same information as the {@code server} parameter.
     */
    public static String create(ServerSettings server) {
        String userEnc = encodeUtf8(server.username);
        String passwordEnc = (server.password != null) ? encodeUtf8(server.password) : "";
        String clientCertificateAliasEnc = (server.clientCertificateAlias != null) ?
                encodeUtf8(server.clientCertificateAlias) : "";

        String scheme;
        switch (server.connectionSecurity) {
            case SSL_TLS_REQUIRED:
                scheme = "imap+ssl+";
                break;
            case STARTTLS_REQUIRED:
                scheme = "imap+tls+";
                break;
            default:
            case NONE:
                scheme = "imap";
                break;
        }

        AuthType authType = server.authenticationType;
        String userInfo;
        if (authType == AuthType.EXTERNAL) {
            userInfo = authType.name() + ":" + userEnc + ":" + clientCertificateAliasEnc;
        } else {
            userInfo = authType.name() + ":" + userEnc + ":" + passwordEnc;
        }
        try {
            Map<String, String> extra = server.getExtra();
            String path;
            if (extra != null) {
                boolean autoDetectNamespace = ImapStoreSettings.getAutoDetectNamespace(server);
                String pathPrefix = (autoDetectNamespace) ? null : ImapStoreSettings.getPathPrefix(server);
                path = "/" + (autoDetectNamespace ? "1" : "0") + "|" +
                        ((pathPrefix == null) ? "" : pathPrefix);
            } else {
                path = "/1|";
            }
            return new URI(scheme, userInfo, server.host, server.port, path, null, null).toString();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Can't create ImapStore URI", e);
        }
    }
}
