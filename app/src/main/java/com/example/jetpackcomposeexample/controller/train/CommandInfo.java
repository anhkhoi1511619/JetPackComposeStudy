package com.example.jetpackcomposeexample.controller.train;

public enum CommandInfo {
    CHANGE_STATUS(Use.BOTH, Type.REQUEST,1,false,-1),//Use for main and sub controller
    CHANGE_ROUTE(Use.BOTH, Type.REQUEST,2,false,-1),//Use for main and sub controller
    CHANGE_STOP_SEQUENCE(Use.BOTH, Type.REQUEST,3,false,-1),//Use for main and sub controller
    CHANGE_DIRECTION(Use.BOTH, Type.REQUEST,4,false,-1),//Use for main and sub controller

    STATUS_MAIN_REQUEST(Use.ONLY_MAIN, Type.REQUEST,1,true,0),//Default Command, Every error occurred then this command will be set values()[4]
    STATUS_SUB_RESPONSE(Use.ONLY_MAIN, Type.RESPONSE,1,false,1),
    ROUTE_ID_MAIN_REQUEST(Use.ONLY_MAIN, Type.REQUEST,2,true,10),
    ROUTE_ID_SUB_RESPONSE(Use.ONLY_MAIN, Type.RESPONSE,2,false,11),
    STOP_SEQ_MAIN_REQUEST(Use.ONLY_MAIN, Type.REQUEST,3,true,20),
    STOP_SEQ_SUB_RESPONSE(Use.ONLY_MAIN, Type.RESPONSE,3,false,21),
    DIRECTION_MAIN_REQUEST(Use.ONLY_MAIN, Type.REQUEST,4,true,30),
    DIRECTION_SUB_RESPONSE(Use.ONLY_MAIN, Type.RESPONSE,4,false,31),
    ROUTE_ID_SUB_REQUEST(Use.ONLY_SUB, Type.REQUEST,2,true,160),
    ROUTE_ID_MAIN_RESPONSE(Use.ONLY_SUB, Type.RESPONSE,2,false,161),
    STOP_SEQ_SUB_REQUEST(Use.ONLY_SUB, Type.REQUEST,3,true,176),
    STOP_SEQ_MAIN_RESPONSE(Use.ONLY_SUB, Type.RESPONSE,3,false,177),
    DIRECTION_SUB_REQUEST(Use.ONLY_SUB, Type.REQUEST,4,true,192),
    DIRECTION_MAIN_RESPONSE(Use.ONLY_SUB, Type.RESPONSE,4,false,193);
    final public Use onlyUse;
    final public Type type;
    final public int relatedCommandKey;
    final public boolean isHighPriority;
    final public int parentCommand;
    public static CommandInfo get(int v) {
        if(v == -1) return values()[4];
        for (CommandInfo x : values()) if (x.parentCommand == v) return x;
        return values()[4];
    }
    public static CommandInfo getRequestCommand(CommandInfo commandInfo, boolean isMainController) {
        if(commandInfo.onlyUse != Use.BOTH) return values()[4];//Status Command
        Use onlyUse = isMainController ? Use.ONLY_MAIN : Use.ONLY_SUB;
        for (CommandInfo x : values()) {
            if((commandInfo.relatedCommandKey == x.relatedCommandKey) &&//Choose same Related Key
                    (x.onlyUse == onlyUse) &&// Choose controller Main or Sub
                    (x.type == Type.REQUEST)) //Choose type
                return x;
        }
        return values()[4];
    }
    public static boolean isHighPriority(int v) {
        for (CommandInfo x : values()) if (x.parentCommand == v) return x.isHighPriority;
        return values()[4].isHighPriority;
    }
    public static boolean isExists(int v) {
        for (CommandInfo x : values()) if (x.parentCommand == v) return true;
        return false;
    }
    CommandInfo(Use use, Type type ,int relatedCommandKey,boolean isHighPriority, int parentCommand) {
        this.onlyUse = use;
        this.type = type;
        this.relatedCommandKey = relatedCommandKey;
        this.isHighPriority = isHighPriority;
        this.parentCommand = parentCommand;
    }
}
enum Use{
    ONLY_MAIN,
    ONLY_SUB,
    BOTH,
}
enum Type{
    REQUEST,
    RESPONSE,
}
