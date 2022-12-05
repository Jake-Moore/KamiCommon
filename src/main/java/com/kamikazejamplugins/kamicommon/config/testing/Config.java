package com.kamikazejamplugins.kamicommon.config.testing;

//import com.kamikazejamplugins.kamicommon.config.annotation.ConfigValue;
//import com.kamikazejamplugins.kamicommon.config.data.KamiConfig;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.io.File;
//
//@Getter @Setter
//public class Config extends KamiConfig {
//
//    @ConfigValue(above = "This is a test config")
//    public String cmdHelpPrefix = "Test";
//
//    @ConfigValue(path = "settings", above = {"Test comment"})
//    public String testString = "test";
//
//    @ConfigValue(path = "settings2", above = {"Test comment1\nTest Comment2\nSettings Comment"})
//    public int options_xi = 0;
//
//    @ConfigValue(path = "settings2.options")
//    public int options_y = 0;
//
//    @ConfigValue(path = "settings2.options")
//    public int options_z = 0;
//
//    public static void main(String[] args) throws Exception {
//        KamiConfig.create(Config.class, new File("C:\\Users\\Jake\\Desktop\\test.yml"));
//    }
//}
