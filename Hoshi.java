import com.alibaba.fastjson.JSONObject;
import com.arxanfintech.common.crypto.Crypto;
import com.arxanfintech.common.rest.Api;
import com.arxanfintech.common.rest.Client;
import com.arxanfintech.common.rest.Common;
import com.arxanfintech.common.rest.Request;
import com.arxanfintech.sdk.wallet.Wallet;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Hoshi {
    private Wallet wallet;
    private Client client;
    private Crypto crypto;
    Hoshi(){
        this.client = new Client();
        client.PrivateB64 = "2RPpCLAl0CNiiXMLjUNSC1acqtkvU8+U9MtU2yvo4Vz52m8mW4+UrvqmFosxi/pu/AzpFf+CCQtutYCtKOZFoQ==";
        client.Nonce = "114514";
        client.Creator = "did:axn:124d00f2-ea55-4724-8e58-31680d443628";
        client.CertPath = "/home/suying/Documents/certs";
        client.ApiKey = "4JTOfmEHM1534223148";
        client.Address = "139.198.15.132:9143";
        this.wallet = new Wallet(client);
        String privateKeyPath = client.CertPath + "/users/" + client.ApiKey + "/" + client.ApiKey + ".key";
        String publicCertPath = client.CertPath + "/tls/tls.cert";
        try {
            this.crypto = new Crypto(new FileInputStream(privateKeyPath), new FileInputStream(publicCertPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Hoshi hoshi=new Hoshi();
        System.out.println(hoshi.getSelfInf("User5543"));
    }
    /**
     * 注册
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
            return this.wallet.QueryWalletBalance(jsonHeader, "did:axn:kwsxz" + access);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 上传个人信息
     * @param jsonObject 待上传信息
     * @param access 用户名
     * @param name 待上传信息的名称
     * @param privateKeyBase64 用户私钥（注册时返回的私钥，需保存好）
     * @return 返回结果信息，以String存储，需要转换为json数据结构，返回值需判断返回代码是否为零
     */
    public String uploadPOE(JSONObject jsonObject,String access,String name,String privateKeyBase64){
        try {
            JSONObject jsonHeader = new JSONObject();
            jsonHeader.put("Bc-Invoke-Mode", "sync");

            String POEString = jsonObject.toJSONString();
            byte[] bytes = POEString.getBytes("UTF-8");
            Base64 base64 = new Base64();
            String encodedText = base64.encodeToString(bytes);

            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("id", "");
            jsonPayload.put("name", name);
            jsonPayload.put("hash", "");
            jsonPayload.put("parent_id", "");
            jsonPayload.put("owner", "did:axn:kwsxz" + access);
            jsonPayload.put("metadata", encodedText);
            return this.wallet.CreatePOE(jsonHeader,
                    jsonPayload,
                    "did:axn:kwsxz" + access,
                    null,
                    null,
                    privateKeyBase64,
                    ""
            );
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 更改个人信息
     *
     * @param jsonObject       待上传信息
     * @param access           用户名
     * @param privateKeyBase64 用户私钥（注册时返回的私钥，需保存好）
     * @param POEdid           待上传信息的did
     * @return 返回结果信息，以String存储，需要转换为json数据结构，返回值需判断返回代码是否为零
     */
    public String updatePOE(JSONObject jsonObject, String access, String privateKeyBase64, String POEdid) {
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("Bc-Invoke-Mode", "sync");

        String POEString = jsonObject.toJSONString();
        try {
            byte[] bytes = POEString.getBytes("UTF-8");

            Base64 base64 = new Base64();
            String encodedText = base64.encodeToString(bytes);

            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("id", POEdid);
            jsonPayload.put("name", "");
            jsonPayload.put("hash", "");
            jsonPayload.put("parent_id", "");
            jsonPayload.put("owner", "did:axn:kwsxz" + access);
            jsonPayload.put("metadata", encodedText);

            Request request = new Request();
            request.client = this.client;
            request.body = Common.Build_Body(jsonPayload,
                    "did:axn:kwsxz" + access,
                    null,
                    null,
                    privateKeyBase64,
                    "");
            request.header = jsonHeader;
            request.crypto = this.crypto;
            request.url = "http://" + request.client.Address + "/wallet-ng/v1/poe/update";
            Api api = new Api();
            api.NewHttpClient();
            String response = api.DoPut(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
