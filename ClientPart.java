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
     * ע��
     *
     * @param access �û���
     * @param secret ����
     * @param type   �û�����(Organization Person Dependent Independent)
     * @return ���ؽ����Ϣ������ֵ���жϷ��ش����Ƿ�Ϊ��
     */
    public JSONObject signUp(String access, String secret, String type) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("access", access);
        jsonData.put("secret", secret);
        jsonData.put("type", type);
        jsonData.put("id", "did:axn:kwsxz" + access);//��accessǰ����did:axn:kwsxz��Ϊǰ׺����ID
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
     * ��¼
     *
     * @param access �û���
     * @param secret ����
     * @return ���ؽ����Ϣ������ֵ���жϷ��ش����Ƿ�Ϊ��
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

            URL url = new URL("http://139.198.15.132:9143/fred/v1/auth/token"); //url��ַ

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
     * ��ȡ��Ϣ
     *
     * @param access �û���
     * @return ���ؽ����Ϣ����POEdid��key_pair����
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
     * �ϴ�������Ϣ
     *
     * @param jsonObject ���ϴ���Ϣ
     * @param access     �û���
     * @param name       ���ϴ���Ϣ������
     * @return ���ؽ����Ϣ������ֵ���жϷ��ش����Ƿ�Ϊ��
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
     * ���ĸ�����Ϣ
     *
     * @param jsonObject ���ϴ���Ϣ
     * @param access     �û���
     * @param name       ���ϴ���Ϣ������
     * @return ���ؽ����Ϣ������ֵ���жϷ��ش����Ƿ�Ϊ��
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
     * ��ѯ��Ϣ��ɣ��鿴��Щ�����Լ�������������Ϣ
     *
     * @param selfAccess �û���
     * @return ��ѯ���û���
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
     * @param jsonArray  ������ʵ��û��б�
     * @param selfAccess �Լ����û���
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
     * @param access      �û���
     * @param jsonPayload �û���Ϣ
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
     * @return ��Ϣ����������Ϣ
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
     * @param selfAccess  �Լ��û���������������ʣ��û���ǰ��ΪON�����������û���ǰΪOFF
     * @param otherAccess Ҫ��ѯ�û����û���
     * @return ��Ϊ����ɷ��ʣ���Ϊ����δ��ͬ����ʻ���ʱδ��ͬ�����
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
     * @return ��ѯ����������Ϣ
     */
    private JSONObject getPermissionBoxInformation() throws Exception {
        JSONObject jsonObject = this.wallet.QueryPOE(this.header, "did:axn:a20b40ff-ccff-4f7a-a839-acbde84b6a6e");
        String rePayload = jsonObject.getString("Payload");
        String metadataMessage = JSON.parseObject(rePayload).getString("metadata");
        return this.analyzeMetadata(metadataMessage);
    }

    /**
     * @param metadataString ����������
     * @return ���������
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
     * @param jsonObject ����������
     * @return ���������
     */
    private String JSONObjectToMetadata(JSONObject jsonObject) throws Exception {
        String string = jsonObject.toJSONString();
        byte[] bytes = string.getBytes("UTF-8");
        Base64 base64 = new Base64();
        return base64.encodeToString(bytes);
    }
}