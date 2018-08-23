import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.arxanfintech.common.rest.Client;
import com.arxanfintech.sdk.wallet.Wallet;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientPart {

    private Wallet wallet;
    private Client client;
    //private Crypto crypto;
    private JSONObject header;

    private ClientPart() {
        this.client = new Client("IoZYarPTp1532411905",
                "C:/Python27/Lib/site-packages/py_common-2.0.1-py2.7.egg/cryption/ecc/certs",
                "did:axn:124d00f2-ea55-4724-8e58-31680d443628",
                null,
                null,
                "2RPpCLAl0CNiiXMLjUNSC1acqtkvU8+U9MtU2yvo4Vz52m8mW4+UrvqmFosxi/pu/AzpFf+CCQtutYCtKOZFoQ==",
                "139.198.15.132:9143",
                true);
        this.wallet = new Wallet(client);
//        String privateKeyPath = this.client.GetCertPath() + "/users/" + this.client.GetApiKey() + "/" + this.client.GetApiKey() + ".key";
//        String publicCertPath = this.client.GetCertPath() + "/tls/tls.cert";
        this.header = new JSONObject();
        this.header.put("Bc-Invoke-Mode", "sync");
//        try {
//            this.crypto = new Crypto(new FileInputStream(privateKeyPath), new FileInputStream(publicCertPath));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        ClientPart clientPart = new ClientPart();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "rrr");
//        System.out.println(clientPart.uploadPOE(jsonObject,
//                "User226",
//                "first"));
        try {
            String response = clientPart.wallet.QueryPOE(clientPart.header, "did:axn:a20b40ff-ccff-4f7a-a839-acbde84b6a6e").getString("Payload");
            System.out.println(clientPart.analyzeMetadata(JSON.parseObject(response).getString("metadata")));
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(clientPart.getMessageBoxInformation());
//        System.out.println(clientPart.signUp("User226","Password226","Person"));
//        try {
//            System.out.println(clientPart.updatePOE(jsonObject,
//                    "User226",
//                    "first"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        try {
//            JSONObject jsonPayload = new JSONObject();
//            jsonPayload.put("id", "");
//            jsonPayload.put("name", "PermissionBox");
//            jsonPayload.put("owner", clientPart.client.GetCreator());
//            jsonPayload.put("hash", "");
//            jsonPayload.put("metadata", "");
//            System.out.println(clientPart.wallet.CreatePOE(clientPart.header,
//                    jsonPayload,
//                    clientPart.client.GetCreator(),
//                    null,
//                    null,
//                    clientPart.client.GetPrivateB64(),
//                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    /**
     * 注册
     *
     * @param access 用户名
     * @param secret 密码
     * @param type   用户类型(Organization Person Dependent Independent)
     * @return 返回结果信息，返回值需判断返回代码是否为零
     */
    public JSONObject signUp(String access, String secret, String type) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("access", access);
        jsonData.put("secret", secret);
        jsonData.put("type", type);
        jsonData.put("id", "did:axn:kwsxz" + access);//在access前加上did:axn:kwsxz作为前缀当作ID
        try {
            JSONObject response = wallet.Register(this.header, jsonData);
            JSONObject jsonKeyPair = JSON.parseObject(response.getString("Payload")).getJSONObject("key_pair");
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("key_pair", jsonKeyPair);
            jsonMessage.put("POEdid", "");
            this.updateMessageBox(access, jsonMessage);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 登录
     *
     * @param access 用户名
     * @param secret 密码
     * @return 返回结果信息，返回值需判断返回代码是否为零
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
            return null;
        }
    }

    /**
     * 获取信息
     *
     * @param access 用户名
     * @return 返回结果信息，有POEdid和key_pair两项
     */
    public JSONObject getUserInformation(String access) {
        try {
            JSONObject jsonObject = this.getMessageBoxInformation();
            return jsonObject.getJSONObject(access);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传个人信息
     *
     * @param jsonObject 待上传信息
     * @param access     用户名
     * @param name       待上传信息的名称
     * @return 返回结果信息，返回值需判断返回代码是否为零
     */
    public JSONObject uploadPOE(JSONObject jsonObject, String access, String name) {
        try {
            String encodedText = this.JSONObjectToMetadata(jsonObject);

            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("id", "");
            jsonPayload.put("name", name);
            jsonPayload.put("hash", "");
            jsonPayload.put("parent_id", "");
            jsonPayload.put("owner", "did:axn:kwsxz" + access);
            jsonPayload.put("metadata", encodedText);
            JSONObject response = this.wallet.CreatePOE(this.header,
                    jsonPayload,
                    "did:axn:kwsxz" + access,
                    null,
                    null,
                    this.getMessageBoxInformation().getJSONObject(access).getJSONObject("key_pair").getString("private_key"),
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"
            );
            String rePayload = response.getString("Payload");
            String POEdid = JSON.parseObject(rePayload).getString("id");
            JSONObject reMessage = this.getMessageBoxInformation().getJSONObject(access);
            reMessage.put("POEdid", POEdid);
            this.updateMessageBox(access, reMessage);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更改个人信息
     *
     * @param jsonObject 待上传信息
     * @param access     用户名
     * @param name       待上传信息的名称
     * @return 返回结果信息，返回值需判断返回代码是否为零
     */
    public JSONObject updatePOE(JSONObject jsonObject, String access, String name) {
        try {
            String encodedText = this.JSONObjectToMetadata(jsonObject);

            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("id", this.getMessageBoxInformation().getJSONObject(access).getString("POEdid"));
            jsonPayload.put("name", name);
            jsonPayload.put("hash", "");
            jsonPayload.put("parent_id", "");
            jsonPayload.put("owner", "did:axn:kwsxz" + access);
            jsonPayload.put("metadata", encodedText);

            //            Request request = new Request();
//            request.client = this.client;
//            request.body = Common.BuildBody(jsonPayload,
//                    "did:axn:kwsxz" + access,
//                    null,
//                    null,
//                    privateKeyBase64,
//                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"
//            );
//            request.header = jsonHeader;
//            request.crypto = this.crypto;
//            request.url = "http://" + request.client.GetAddress() + "/wallet-ng/v1/poe/update";
//            Api api = new Api();
//            api.NewHttpClient();
//            String response = api.DoPut(request);
            return this.wallet.UpdatePOE(this.header,
                    jsonPayload,
                    this.client.GetCreator(),
                    null,
                    null,
                    this.getMessageBoxInformation().getJSONObject(access).getJSONObject("key_pair").getString("private_key"),
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 查询信息许可，查看哪些人向自己发来了请求信息
     *
     * @param selfAccess 用户名
     * @return 查询者用户名
     */
    public JSONArray getPermissionFromWho(String selfAccess) {
        try {
            JSONObject jsonObject = this.getPermissionBoxInformation();
            if (jsonObject.containsKey(selfAccess)) {
                return jsonObject.getJSONArray(selfAccess);
            } else {
                return new JSONArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param jsonArray  请求访问的用户列表
     * @param selfAccess 自己的用户名
     */
    public void updatePermissionBox(JSONArray jsonArray, String selfAccess) {
        try {
            JSONObject jsonObject = this.getPermissionBoxInformation();
            jsonObject.put(selfAccess, jsonArray);
            String string = this.JSONObjectToMetadata(jsonObject);

            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("id", "did:axn:a20b40ff-ccff-4f7a-a839-acbde84b6a6e");
            jsonPayload.put("name", "PermissionBox");
            jsonPayload.put("owner", this.client.GetCreator());
            jsonPayload.put("hash", "");
            jsonPayload.put("metadata", string);

            System.out.println(this.wallet.UpdatePOE(this.header,
                    jsonPayload,
                    this.client.GetCreator(),
                    null,
                    null,
                    this.client.GetPrivateB64(),
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param access      用户名
     * @param jsonPayload 用户信息
     */
    public void updateMessageBox(String access, JSONObject jsonPayload) {
        try {
            JSONObject jsonMetadataMessage = this.getMessageBoxInformation();
            jsonMetadataMessage.put(access, jsonPayload);
            String stringMetadataMessage = this.JSONObjectToMetadata(jsonMetadataMessage);

            JSONObject jsonPOEPayload = new JSONObject();
            jsonPOEPayload.put("id", "did:axn:af7380cb-2137-4c2d-a98e-62aba671f6df");
            jsonPOEPayload.put("name", "MessageBox");
            jsonPOEPayload.put("owner", this.client.GetCreator());
            jsonPOEPayload.put("hash", "");
            jsonPOEPayload.put("metadata", stringMetadataMessage);

            System.out.println(this.wallet.UpdatePOE(this.header,
                    jsonPOEPayload,
                    this.client.GetCreator(),
                    null,
                    null,
                    this.client.GetPrivateB64(),
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 信息中心所有信息
     */
    public JSONObject getMessageBoxInformation() {
        try {
            JSONObject jsonObject = this.wallet.QueryPOE(this.header, "did:axn:af7380cb-2137-4c2d-a98e-62aba671f6df");
            String rePayload = (String) jsonObject.get("Payload");
            String metadataMessage = (String) JSON.parseObject(rePayload).get("metadata");
            return this.analyzeMetadata(metadataMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param selfAccess  自己用户名，若是允许访问，用户名前面为ON，若不允许，用户名前为OFF
     * @param otherAccess 要查询用户的用户名
     * @return 若为真则可访问，若为否则未被同意访问或暂时未被同意访问
     */
    public boolean askPermissionBox(String selfAccess, String otherAccess) {
        try {
            JSONObject jsonMetadataMessage = this.getPermissionBoxInformation();
            if (jsonMetadataMessage.containsKey(otherAccess)) {
                if (jsonMetadataMessage.getJSONArray(otherAccess).contains("OFF" + selfAccess)) {
                    return false;
                } else if (jsonMetadataMessage.getJSONArray(otherAccess).contains("ON" + selfAccess)) {
                    return true;
                } else {
                    jsonMetadataMessage.getJSONArray(otherAccess).add("OFF" + selfAccess);
                    jsonMetadataMessage.put(otherAccess, jsonMetadataMessage.getJSONArray(otherAccess));
                }
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.add("OFF" + selfAccess);
                jsonMetadataMessage.put(otherAccess, jsonArray);
            }
            String stringMetadataMessage = this.JSONObjectToMetadata(jsonMetadataMessage);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", "did:axn:a20b40ff-ccff-4f7a-a839-acbde84b6a6e");
            jsonObject.put("name", "PermissionBox");
            jsonObject.put("owner", this.client.GetCreator());
            jsonObject.put("hash", "");
            jsonObject.put("metadata", stringMetadataMessage);

            System.out.println(this.wallet.UpdatePOE(this.header,
                    jsonObject,
                    this.client.GetCreator(),
                    null,
                    null,
                    this.client.GetPrivateB64(),
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * @return 查询中心所有信息
     */
    private JSONObject getPermissionBoxInformation() throws Exception {
        JSONObject jsonObject = this.wallet.QueryPOE(this.header, "did:axn:a20b40ff-ccff-4f7a-a839-acbde84b6a6e");
        String rePayload = jsonObject.getString("Payload");
        String metadataMessage = JSON.parseObject(rePayload).getString("metadata");
        return this.analyzeMetadata(metadataMessage);
    }

    /**
     * @param metadataString 待解码数据
     * @return 解码后数据
     */
    private JSONObject analyzeMetadata(String metadataString) throws Exception {
        if (metadataString == null) {
            throw new Exception();
        }
        Base64 base64 = new Base64();
        byte[] bytes = base64.decode(metadataString);
        return (JSONObject) JSON.parse(bytes);
    }

    /**
     * @param jsonObject 待编码数据
     * @return 编码后数据
     */
    private String JSONObjectToMetadata(JSONObject jsonObject) throws Exception {
        String string = jsonObject.toJSONString();
        byte[] bytes = string.getBytes("UTF-8");
        Base64 base64 = new Base64();
        return base64.encodeToString(bytes);
    }
}