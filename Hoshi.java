import com.alibaba.fastjson.JSONObject;
import com.arxanfintech.common.rest.Client;
import com.arxanfintech.sdk.wallet.Wallet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Hoshi {
    public Wallet wallet;
    Hoshi(){
        Client client = new Client();
        client.PrivateB64 = "2RPpCLAl0CNiiXMLjUNSC1acqtkvU8+U9MtU2yvo4Vz52m8mW4+UrvqmFosxi/pu/AzpFf+CCQtutYCtKOZFoQ==";
        client.Nonce = "114514";
        client.Creator = "did:axn:124d00f2-ea55-4724-8e58-31680d443628";
        client.CertPath = "/home/suying/Documents/certs";
        client.ApiKey = "4JTOfmEHM1534223148";
        client.Address = "139.198.15.132:9143";
        this.wallet = new Wallet(client);
    }
    public static void main(String[] args) {
        Hoshi hoshi=new Hoshi();
        System.out.println(hoshi.getSelfInf("User5543"));
    }
    /**
     * 注册
     *
     * @param access 用户名
     * @param secret 密码
     * @param type   用户类型(Organization Person Dependent Independent)
     * @return 返回结果信息，以String存储，需要转换为json数据结构，返回值需判断返回代码是否为零
     */
    public String signUp(String access, String secret, String type) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("access", access);
        jsonData.put("secret", secret);
        jsonData.put("type", type);
        jsonData.put("id", "did:axn:kwsxz" + access);//在access前加上did:axn:kwsxz作为前缀当作ID
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("Bc-Invoke-Mode", "sync");
        String response;
        try {
            return wallet.Register(jsonHeader, jsonData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 登录
     *
     * @param access 用户名
     * @param secret 密码
     * @return 返回结果信息，以String存储，需要转换为json数据结构，返回值需判断返回代码是否为零
     */
    public String logIn(String access, String secret) {
        try {
            JSONObject jsonValue = new JSONObject();
            jsonValue.put("access", access);
            jsonValue.put("secret", secret);
            JSONObject jsonCredential = new JSONObject();
            jsonCredential.put("value", jsonValue);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("credential", jsonCredential);
            String param = jsonObject.toJSONString();

            URL url = new URL("http://139.198.15.132:9143/fred/v1/auth/token"); //url地址

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();


            OutputStream os = connection.getOutputStream();
            os.write(param.getBytes("UTF-8"));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sbf = new StringBuffer();
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sbf.append(lines);
            }
            String resp = sbf.toString();
            connection.disconnect();
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取信息
     * @param access 用户名
     * @return 返回结果信息，以String存储，需要转换为json数据结构
     */
    public String getSelfInf(String access) {
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("Bc-Invoke-Mode", "sync");
        try {
            System.out.println(this.wallet.QueryWalletBalance(jsonHeader, "did:axn:kwsxz" + access));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
