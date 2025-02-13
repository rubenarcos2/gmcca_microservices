package com.rarcos.gmcca_notifications.events;

import com.rarcos.gmcca_notifications.model.enums.DocProcessStatus;

public record DocProcessEvent(String fileName, DocProcessStatus docProcessStatus){
}
