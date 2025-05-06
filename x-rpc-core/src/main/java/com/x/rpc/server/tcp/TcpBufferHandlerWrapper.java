package com.x.rpc.server.tcp;

import com.x.rpc.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

/**
 * @author lingpfeng
 * @date 2025/04/30 09:00:01
 * @description  装饰器模式(是使用recordParser对原有的buffer处理能力进行增强)
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser recordParser;

    public TcpBufferHandlerWrapper(Handler<Buffer> handler) {
        recordParser = initRecordParser(handler);
    }

    /**
     * @param handler
     * @return
     * @description  这个recordParser是用来处理tcp粘包和拆包的，
     * 它会将接收到的buffer按照指定的长度进行拆分，然后再交给handler进行处理，
     * 这样就可以保证每次接收到的数据都是完整的，不会出现粘包和拆包的问题。
     */
    private RecordParser initRecordParser(Handler<Buffer> handler) {
        //构造parser
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<Buffer>() {
            // 初始化 (用于判断是否是第一个包)
            int size = -1;
            // 一次完整的读取
            Buffer resultBuffer = Buffer.buffer();
            @Override
            public void handle(Buffer buffer) {
                if(-1 == size){  // 第一次读取
                    //头部信息 [magic(1byte) + version(1byte) + serializer(1byte) + type(1byte) + status(1byte) + requestId(long 8byte)]  = 13 + bytes bodySize(int 4byte)
                    size = buffer.getInt(13);
                    // 重置触发handle的buffer大小
                    parser.fixedSizeMode(size);
                    //写入头部消息到结果buffer
                    resultBuffer.appendBuffer(buffer);
                }else { // 第二次读取
                    //写入消息体到结果buffer
                    resultBuffer.appendBuffer(buffer);
                    //处理
                    handler.handle(resultBuffer);
                    //重置
                    size = -1;
                    resultBuffer = Buffer.buffer();
                    parser.fixedSizeMode(ProtocolConstant.MESSAGE_HEADER_LENGTH);
                }
            }
        });
        return parser;
    }


    @Override
    public void handle(Buffer buffer) {
        recordParser.handle(buffer);
    }
}
