namespace java com.git.lee.rpc.thrift.demo.service

struct Message {
    1: i64 timestamp,
    2: string name,
    3: map<string,string> tags,
    4: list<Message> children,
}
service MessageService{
    oneway void send(1:Message message)
}