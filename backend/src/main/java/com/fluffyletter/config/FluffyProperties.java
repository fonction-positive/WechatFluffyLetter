package com.fluffyletter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fluffy")
public class FluffyProperties {

    private final Wechat wechat = new Wechat();
    private final Jwt jwt = new Jwt();
    private final Admin admin = new Admin();
    private final Upload upload = new Upload();

    public Wechat getWechat() {
        return wechat;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Admin getAdmin() {
        return admin;
    }

    public Upload getUpload() {
        return upload;
    }

    public static class Wechat {
        private String appid;
        private String secret;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }

    public static class Jwt {
        private String secret;
        private long ttlSeconds = 2592000;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }

    public static class Admin {
        private final Bootstrap bootstrap = new Bootstrap();

        public Bootstrap getBootstrap() {
            return bootstrap;
        }

        public static class Bootstrap {
            /**
             * 开发期可用：启动时如果 admin_user 表为空，则创建一个初始管理员。
             * 为避免误用，默认不创建；只有显式配置 username/password 才会创建。
             */
            private String username;
            private String password;
            private String role = "superadmin";

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }
        }
    }

    public static class Upload {
        /**
         * 本地上传文件存储根目录。
         * 例如：/data/uploads（Docker 建议挂载 volume）
         */
        private String dir = "uploads";

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
}
