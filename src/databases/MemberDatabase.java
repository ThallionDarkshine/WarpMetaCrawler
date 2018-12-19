package databases;

import gameData.Member;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ThallionDarkshine on 9/12/2018.
 */
public class MemberDatabase {
    private static Map<String, Member> members = new HashMap<>();

    public static boolean has(String  memberName) {
        return members.containsKey(memberName);
    }

    public static void add(Member member) {
        String memName = member.getName();

        if (!has(memName)) {
            members.put(memName, member);
        }
    }

    public static Member get(String memberName) {
        return members.get(memberName);
    }
}
