package com.rarcos.gmcca_ia_rag.model.dtos;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "files")
@Slf4j
public class FileData {
    @Id
    private String id;
    @Getter
    private String name;
    @Getter
    private String type;
    @Getter
    private byte[] content;

    public static class Builder{
        private String id;
        private String name;
        private String type;
        private byte[] content;

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder type(String type){
            this.type = type;
            return this;
        }

        public Builder content(byte[] content){
            this.content = content;
            return this;
        }

        public FileData build(){
            return new FileData();
        }
    }

    private FileData(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.content = builder.content;
    }
}
