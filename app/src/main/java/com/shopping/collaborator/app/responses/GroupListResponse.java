package com.shopping.collaborator.app.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by raajesharunachalam on 8/11/17.
 */

public class GroupListResponse {
    @SerializedName("groups")
    private Group[] groups;

    public Group[] getGroups(){
        return groups;
    }
}
