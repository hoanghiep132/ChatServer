package com.hiepnh.chatserver.netty_server.inbound;

import com.hiepnh.chatserver.common.BufferConstant;
import com.hiepnh.chatserver.common.StateDecoder;
import com.hiepnh.chatserver.excutor.PackageHandler;
import com.hiepnh.chatserver.model.TlvPackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestDecoderHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PackageHandler packageHandler;

    public RequestDecoderHandler(PackageHandler packageHandler) {
        this.packageHandler = packageHandler;
    }

    private ByteBuf tmp;

    private static int flag = 1;

    private TlvPackage tlvPackage = new TlvPackage();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        tmp = ctx.alloc().buffer(BufferConstant.KB);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        tmp.writeBytes(buf);
        buf.release();
        int byteNumber = tmp.readableBytes();
        int start = 0;

        while (byteNumber > 0){
            switch (flag){
                case StateDecoder.TAG_FLAG:
                    byte tagByte = tmp.getByte(start++);
                    tlvPackage.setTag(tagByte);
                    byteNumber -= StateDecoder.TAG_LENGTH;
                    flag = StateDecoder.LENGTH_FLAG;
                    break;
                case StateDecoder.LENGTH_FLAG:
                    if(byteNumber < StateDecoder.LENGTH){
                        return;
                    }
                    int length = tmp.getInt(start);
                    tlvPackage.setLength(length);
                    start += StateDecoder.LENGTH;
                    byteNumber -= StateDecoder.LENGTH;
                    flag = StateDecoder.VALUE_FLAG;
                    break;
                case StateDecoder.VALUE_FLAG:
                    int valueLength = tlvPackage.getLength() - tlvPackage.getCurrent();
                    if(byteNumber < valueLength){
                        byte[] values = new byte[byteNumber];
                        tmp.getBytes(start, values);
                        tlvPackage.setData(values);
                        tmp.clear();
                        return;
                    }
                    byte[] values = new byte[valueLength];
                    tmp.getBytes(start, values);
                    tlvPackage.setData(values);
                    packageHandler.addPackage(tlvPackage, ctx.channel());
                    start += valueLength;
                    byteNumber -= valueLength;
                    tlvPackage.reset();
                    flag = StateDecoder.TAG_FLAG;
                    break;
            }
        }
        tmp.clear();
    }
}
