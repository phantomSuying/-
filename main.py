import json
import base64
import requests
from api.wallet import WalletClient
from rest.api.api import Client


class ClientPart:
    def __init__(self):
        self.api_key = "IoZYarPTp1532411905"
        self.cert_path = "C:/Python27/Lib/site-packages/py_common-2.0.1-py2.7.egg/cryption/ecc/certs"
        self.ent_sign_param = {
            "creator": "did:axn:124d00f2-ea55-4724-8e58-31680d443628",
            "created": "",
            "nonce": "",
            "privateB64": "2RPpCLAl0CNiiXMLjUNSC1acqtkvU8+U9MtU2yvo4Vz52m8mW4+UrvqmFosxi/pu/AzpFf+CCQtutYCtKOZFoQ=="
        }
        self.ip_address = "http://139.198.15.132:9143"

        self.client = Client(self.api_key, self.cert_path, self.ent_sign_param, self.ip_address)
        self.wallet = WalletClient(self.client)
        self.header = {
            "Bc-Invoke-Mode": "sync"
        }
        self.message_center = "did:axn:af7380cb-2137-4c2d-a98e-62aba671f6df"
        self.permission_center = "did:axn:a20b40ff-ccff-4f7a-a839-acbde84b6a6e"

    def sign_up(self, access, secret, entity_type, name, country):
        body = {
            "id": "did:axn:kwsxz" + access,
            "type": entity_type,
            "access": access,
            "secret": secret
        }
        _, response = self.wallet.register(self.header, body)
        if response["ErrCode"] == 0:
            key_pair = json.loads(response["Payload"])["key_pair"]
            private_key = key_pair["private_key"]

            message = {
                "name": name,
                "country": country
            }

            response_poe = self.__create_poe(access, private_key, message)
            poe_did = json.loads(response_poe["Payload"])["id"]

            response_message = self.__query_poe_to_json(self.message_center)
            response_message[access] = {
                "POEdid": poe_did,
                "key_pair": key_pair
            }
            self.__update_poe_metadata(response_message, self.message_center, "MessageBox",
                                       self.ent_sign_param["creator"],
                                       self.ent_sign_param["privateB64"])
            return response
        else:
            return None

    def log_in(self, access, secret):
        response = requests.post(url="http://139.198.15.132:9143/fred/v1/auth/token", json={
            "credential": {
                "value": {
                    "access": access,
                    "secret": secret
                }
            }
        })
        return response.json()

    def query_personal_message(self, access):
        try:
            personal_poe_did = self.__query_poe_to_json(self.message_center)[access]["POEdid"]
        except Exception:
            return None
        else:
            return self.__query_poe_to_json(personal_poe_did)

    def update_personal_message(self, access, message):
        try:
            personal_value = self.__query_poe_to_json(self.message_center)[access]
        except Exception:
            return None
        else:
            return self.__update_poe_metadata(message, personal_value["POEdid"], "PersonalMessage",
                                              "did:axn:kwsxz" + access,
                                              personal_value["key_pair"]["private_key"])

    def ask_other_message(self, self_access, other_access):

        response = self.__query_poe_to_json(self.permission_center)
        if other_access in response:
            for x in response[other_access]:
                if x["userAccess"] == self_access:
                    return x["permitList"]
            response[other_access].append({
                "userAccess": self_access,
                "permitList": []
            })
        else:
            response[other_access] = []
        self.__update_poe_metadata(response, self.permission_center, "PermissionBox", self.ent_sign_param["creator"],
                                   self.ent_sign_param["privateB64"])

    def update_permission_list(self, access, permit_list, other_access):

        response = self.__query_poe_to_json(self.permission_center)
        if access in response:
            found = False
            for x in response[access]:  # jsonArray
                if x["userAccess"] == other_access:
                    x["permitList"] = permit_list
                    found = True
            if not found:
                response[access].append({
                    "userAccess": other_access,
                    "permitList": permit_list
                })
        else:
            response[access] = [{
                "userAccess":other_access,
                "permitList":permit_list
            }]
        self.__update_poe_metadata(response, self.permission_center, "PermissionBox", self.ent_sign_param["creator"],
                                   self.ent_sign_param["privateB64"])

    def query_personal_permission(self, access):
        json_object = self.__query_poe_to_json(self.permission_center)
        ls = []
        if access in json_object:
            for x in json_object[access]:
                ls.append(x["userAccess"])
            return ls
        else:
            return None

    def __create_poe(self, access, private_key, message):
        pay_load = {
            "name": "PersonalMessage",
            "owner": "did:axn:kwsxz" + access,
            "metadata": base64.b64encode(json.dumps(message))
        }
        parameter = {
            "creator": self.ent_sign_param["creator"],
            "created": "",
            "nonce": "",
            "privateB64": private_key,
            "payload": pay_load,
        }
        _, response = self.wallet.create_poe(self.header, pay_load, parameter)
        return response

    def __query_poe_to_json(self, poe_did):
        _, response = self.wallet.query_poe(self.header, poe_did)
        return json.loads(base64.b64decode(json.loads(response["Payload"])["metadata"]))

    def __update_poe_metadata(self, message, poe_did, name, did, private_key):
        payload = {
            "id": poe_did,
            "name": name,
            "owner": did,
            "metadata": base64.b64encode(json.dumps(message))
        }
        parameter = {
            "creator": self.ent_sign_param["creator"],
            "created": "",
            "nonce": "",
            "privateB64": private_key,
            "payload": payload,
        }
        _, response = self.wallet.update_poe(self.header, payload, parameter)
        return response

