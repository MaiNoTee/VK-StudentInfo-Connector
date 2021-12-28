import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.Fields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VK_API {

    private static final String TOKEN = "96b0530deaf5a2eb79011a595c00fea024cb5faa0fbab62bb4ac4625c69d8f25410497c0c69ba7a378b3b";
    private static final int APP_ID = 8001605;
    private final VkApiClient vk;
    private final UserActor actor;

    public VK_API() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        actor = new UserActor(APP_ID, TOKEN);
    }

    public List<JsonObject> findUsers(String groupID) throws ClientException, ApiException {
        var memberIds = getMemberIds(groupID);
        var members = new ArrayList<JsonObject>(memberIds.size());
        var maxCount = 0;
        while (maxCount < memberIds.size()) {
            var temp = new JsonParser().parse(vk.users().get(actor)
                    .userIds(memberIds.subList(maxCount, maxCount + (memberIds.size() - maxCount) % 1000))
                    .fields(new Fields[]{Fields.BDATE, Fields.CITY, Fields.PHOTO_MAX, Fields.SEX}).execute().toString()).getAsJsonArray();
            for (var i : temp) {
                members.add(i.getAsJsonObject());
            }
            maxCount += 1000;
        }
        return members;
    }

    private List<String> getMemberIds(String groupID) throws ApiException, ClientException {
        var response = vk.groups().getMembers(actor).groupId(groupID);
        var temp = new JsonParser().parse(response.execute().toString()).getAsJsonObject();
        var maxElements = temp.get("count").getAsInt();
        var memberIds = temp.get("items").getAsJsonArray();
        var members = new HashSet<String>(maxElements);
        for (var memberId : memberIds) {
            members.add(memberId.getAsString());
        }
        while (maxElements > 0) {
            temp = new JsonParser().parse(response.offset(1000).execute().toString()).getAsJsonObject();
            memberIds = temp.get("items").getAsJsonArray();
            for (var i : memberIds) {
                members.add(i.getAsString());
            }
            maxElements -= 1000;
        }
        return new ArrayList<>(members);
    }
}
