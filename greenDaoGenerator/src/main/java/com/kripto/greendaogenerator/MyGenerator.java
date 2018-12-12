package com.kripto.greendaogenerator;


import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class MyGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.kripto.passpocket.db");
        schema.enableKeepSectionsByDefault();

        addPassStorage(schema);

        //new DaoGenerator().generateAll(schema, args[0]);
        try {
            new DaoGenerator().generateAll(schema,"./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addPassStorage(Schema schema){
        Entity passStorage = schema.addEntity("PassStorage");
        passStorage.setHasKeepSections(true);
        passStorage.addLongProperty("id").primaryKey().autoincrement();
        passStorage.addStringProperty("socialMedia");
        passStorage.addStringProperty("username");
        passStorage.addStringProperty("password");
    }

}
