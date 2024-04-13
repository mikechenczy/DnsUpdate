import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Mike_Chen
 * @date 2023/7/6
 * @apiNote
 */
public class Main {
    //TODO 填入你的相关信息
    /*用得到以下命令
        curl -X GET "https://api.cloudflare.com/client/v4/zones/zoneId/dns_records?page=1&per_page=20&order=type&direction=asc" \
     -H "Content-Type:application/json" \
     -H "X-Auth-Key: authKey" \
     -H "X-Auth-Email: authEmail"
         */
    public static String zoneId = "";
    public static String dnsId = "";
    public static String authEmail = "";
    public static String authKey = "";
    public static String name = "";

    public static void main(String[] args) {
        if(args.length==3) {
            String ipv6 = args[0].replaceAll("\r", "").replaceAll(" ", "").replaceAll("\n", "");//runProcess("ip addr show dev eth0 | sed -e's/^.*inet6 \\([^ ]*\\)\\/.*$/\\1/;t;d' | awk 'NR==1{print $1}'");
            System.out.println(ipv6);
            dnsId = args[1].replaceAll("\r", "").replaceAll(" ", "").replaceAll("\n", "");
            System.out.println(dnsId);
            name = args[2].replaceAll("\r", "").replaceAll(" ", "").replaceAll("\n", "");
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            String content = "{\"type\":\"AAAA\",\"name\":\""+name+"\",\"content\":\"" + ipv6 + "\",\"ttl\":60,\"proxied\":false}";
            System.out.println(content);
            RequestBody body = RequestBody.create(mediaType, content);
            Request request = new Request.Builder()
                    .url("https://api.cloudflare.com/client/v4/zones/"+zoneId+"/dns_records/"+dnsId)
                    .put(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Auth-Email", authEmail)
                    .addHeader("X-Auth-Key", authKey)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if(args.length==0) {
            while (true) {
                String ipv6 = getComputerPublicIPV6();
                System.out.println(ipv6);
                if(ipv6!=null) {
                    OkHttpClient client = new OkHttpClient();
                    MediaType mediaType = MediaType.parse("application/json");
                    String content = "{\"type\":\"AAAA\",\"name\":\""+name+"\",\"content\":\"" + ipv6 + "\",\"ttl\":60,\"proxied\":false}";
                    System.out.println(content);
                    RequestBody body = RequestBody.create(mediaType, content);
                    Request request = new Request.Builder()
                            .url("https://api.cloudflare.com/client/v4/zones/"+zoneId+"/dns_records/"+dnsId)
                            .put(body)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("X-Auth-Email", authEmail)
                            .addHeader("X-Auth-Key", authKey)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        System.out.println(response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000*60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getComputerPublicIPV6() {
        try {
            for (InetAddress it : InetAddress.getAllByName(InetAddress.getLocalHost().getHostName())) {
                if(it.getHostAddress().startsWith("2")) {
                    return it.getHostAddress();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
