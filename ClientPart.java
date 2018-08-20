import com.alibaba.fastjson.JSON;
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

public class ClientPart {

    private Wallet wallet;
    private Client client;
    private Crypto crypto;

    public ClientPart() {
        this.client = new Client();
        this.client.PrivateB64 = "2RPpCLAl0CNiiXMLjUNSC1acqtkvU8+U9MtU2yvo4Vz52m8mW4+UrvqmFosxi/pu/AzpFf+CCQtutYCtKOZFoQ==";
        this.client.Nonce = "123456";
        this.client.Creator = "did:axn:124d00f2-ea55-4724-8e58-31680d443628";
        this.client.CertPath = "C:/Python27/Lib/site-packages/py_common-2.0.1-py2.7.egg/cryption/ecc/certs";
        this.client.ApiKey = "IoZYarPTp1532411905";
        this.client.Address = "139.198.15.132:9143";
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
        ClientPart clientPart = new ClientPart();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "lll");
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("Bc-Invoke-Mode", "sync");
        System.out.println(clientPart.uploadPOE(jsonObject,
                "User551",
                "sixth",
                "nCjQIGsWrm1nhhcD6Wl+Cqwnx0ssg+/aSbxb2VIMxqGMDCtOJET0U9sbTxNDE5Dmirz/oGT7o70SpOOe4kaGcA=="));
//        System.out.println(clientPart.getSelfInf("User551"));
//        try {
//            System.out.println(clientPart.wallet.QueryPOE(jsonHeader, "did:axn:709709c8-01a5-4f4b-8d06-887bee85cf64"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * ע��
     *
     * @param access �û���
     * @param secret ����
     * @param type   �û�����(Organization Person Dependent Independent)
     * @return ���ؽ����Ϣ����String�洢����Ҫת��Ϊjson���ݽṹ������ֵ���жϷ��ش����Ƿ�Ϊ��
     */
    public String signUp(String access, String secret, String type) {
        JSONObject jsonData = new JSONObject();
        jsonData.put("access", access);
        jsonData.put("secret", secret);
        jsonData.put("type", type);
        jsonData.put("id", "did:axn:kwsxz" + access);//��accessǰ����did:axn:kwsxz��Ϊǰ׺����ID
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("Bc-Invoke-Mode", "sync");
        try {
            return wallet.Register(jsonHeader, jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��¼
     *
     * @param access �û���
     * @param secret ����
     * @return ���ؽ����Ϣ����String�洢����Ҫת��Ϊjson���ݽṹ������ֵ���жϷ��ش����Ƿ�Ϊ��
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
        }
        return null;
    }

    /**
     * ��ȡ��Ϣ
     *
     * @param access �û���
     * @return ���ؽ����Ϣ����String�洢����Ҫת��Ϊjson���ݽṹ
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
     * �ϴ�������Ϣ
     *
     * @param jsonObject       ���ϴ���Ϣ
     * @param access           �û���
     * @param name             ���ϴ���Ϣ������
     * @param privateKeyBase64 �û�˽Կ��ע��ʱ���ص�˽Կ���豣��ã�
     * @return ���ؽ����Ϣ����String�洢����Ҫת��Ϊjson���ݽṹ������ֵ���жϷ��ش����Ƿ�Ϊ��
     */
    public String uploadPOE(JSONObject jsonObject, String access, String name, String privateKeyBase64) {
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
            String response = this.wallet.CreatePOE(jsonHeader,
                    jsonPayload,
                    "did:axn:kwsxz" + access,
                    null,
                    null,
                    privateKeyBase64,
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"
            );
            JSONObject jsonTokenPayload = JSON.parseObject("{\"issuer\":\"" +
                    "did:axn:124d00f2-ea55-4724-8e58-31680d443628" +
                    "\",\"owner\":\"" +
                    "did:axn:kwsxz" + access +
                    "\",\"asset_id\":\"" +
                    JSON.parseObject((String) JSON.parseObject(response).get("Payload")).get("id") +
                    "\",\"amount\":1000,\"fee\":{\"amount\":0}}");
            System.out.println(wallet.IssueTokens(jsonHeader,
                    jsonTokenPayload,
                    "did:axn:124d00f2-ea55-4724-8e58-31680d443628",
                    null,
                    null,
                    privateKeyBase64,
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"));
            JSONObject jsonAssetsPayload = JSON.parseObject("{\"issuer\":\"" +
                    "did:axn:124d00f2-ea55-4724-8e58-31680d443628" +
                    "\",\"owner\":\"" +
                    "did:axn:kwsxz" + access +
                    "\",\"asset_id\":\"" +
                    JSON.parseObject((String) JSON.parseObject(response).get("Payload")).get("id") +
                    "\",\"fee\":{\"amount\":0}}");
            System.out.println(wallet.IssueAssets(jsonHeader,
                    jsonAssetsPayload,
                    "did:axn:124d00f2-ea55-4724-8e58-31680d443628",
                    null,
                    null,
                    privateKeyBase64,
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe"));
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ���ĸ�����Ϣ
     *
     * @param jsonObject       ���ϴ���Ϣ
     * @param access           �û���
     * @param privateKeyBase64 �û�˽Կ��ע��ʱ���ص�˽Կ���豣��ã�
     * @param POEdid           ���ϴ���Ϣ��did
     * @return ���ؽ����Ϣ����String�洢����Ҫת��Ϊjson���ݽṹ������ֵ���жϷ��ش����Ƿ�Ϊ��
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
                    "C:/Go/src/github.com/arxanchain/sdk-go-common/crypto/tools/build/bin/sign-util.exe");
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

    /**
     * Ѱ�����
     *
     * @param privateKeyBase64 �û�˽Կ
     * @param otherAccess      �����û���
     * @param selfAccess       �Լ����û���
     * @param tokenId          ����did
     * @return ���ؽ����Ϣ����String�洢����Ҫת��Ϊjson���ݽṹ������ֵ���жϷ��ش����Ƿ�Ϊ��
     */
    public String askForPermission(String selfAccess, String otherAccess, String tokenId, String privateKeyBase64) {
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("Bc-Invoke-Mode", "sync");

        JSONObject jsonPayload = JSON.parseObject("{\"from\":\"" +
                selfAccess +
                "\",\"to\":\"" +
                otherAccess +
                "\",\"tokens\":[{\"token_id\":\"" +
                tokenId +
                "\",\"amount\":5}],\"fee\":{\"amount\":10}}");
        try {
            return this.wallet.TransferTokens(jsonHeader,
                    jsonPayload,
                    selfAccess,
                    null,
                    null,
                    privateKeyBase64,
                    "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * ��ѯ��Ϣ���
     *
     *
     *
     */
    public String getPermission(String access) {
        return null;
    }
}