package me.yura.magebanet.data;

import me.yura.magebanet.MageBanet;
import me.yura.magebanet.datatypes.DataPlayer;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

public class DataManager {

    public static DataManager DATA;

    private HashMap<String, DataPlayer> dataPlayers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public DataManager(MageBanet instance) {
        DATA = this;


        File f0 = new File(instance.getDataFolder() + "/data");

        if(f0.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f0));
                dataPlayers = (HashMap<String, DataPlayer>) in.readObject();
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод сериализации данных
     * @param instance главный класс
     */
    public void saveData(MageBanet instance){
        if(!dataPlayers.isEmpty()){
            File f0 = new File(instance.getDataFolder() + "/data");
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f0, false));
                out.writeObject(dataPlayers);
                out.flush();
                out.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public DataPlayer getPlayer(String name) {
        if(!dataPlayers.containsKey(name)) dataPlayers.put(name, new DataPlayer(name));

        return dataPlayers.get(name);
    }

    public Collection<DataPlayer> getDataPlayers() {
        return dataPlayers.values();
    }
}
