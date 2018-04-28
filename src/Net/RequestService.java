package Net;

/**
 * 解析请求
 */
public interface RequestService {
    /**
     * @param content 请求传入的数据
     * @return 相应请求的数据
     */
    String handleRequest(String content);
}
