package com.rduda.frontend.VertChat.Protocol;

/**
 * Created by Robin on 2015-12-16.
 *
 * Command to retrieve the help list.
 */
public class Help {
    private Header header = new Header("help");

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
