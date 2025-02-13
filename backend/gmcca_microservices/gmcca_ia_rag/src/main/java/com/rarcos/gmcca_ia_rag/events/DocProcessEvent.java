package com.rarcos.gmcca_ia_rag.events;

import com.rarcos.gmcca_ia_rag.model.enums.DocProcessStatus;

public record DocProcessEvent(String fileName, DocProcessStatus docProcessStatus){
}
