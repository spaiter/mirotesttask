package com.miro.api.widgets.testtask.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageDeserializer extends JsonDeserializer<Page<?>> implements ContextualDeserializer {
    private static final String CONTENT = "content";
    private static final String NUMBER = "number";
    private static final String SIZE = "size";
    private static final String TOTAL_ELEMENTS = "totalElements";
    private JavaType valueType;

    @Override
    public Page<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        final CollectionType valuesListType = context.getTypeFactory().constructCollectionType(List.class, valueType);

        List<?> list = new ArrayList<>();
        int pageNumber = 0;
        int pageSize = 0;
        long total = 0;
        if (parser.isExpectedStartObjectToken()) {
            parser.nextToken();
            if (parser.hasTokenId(JsonTokenId.ID_FIELD_NAME)) {
                String propName = parser.getCurrentName();
                do {
                    parser.nextToken();
                    switch (propName) {
                        case CONTENT:
                            list = context.readValue(parser, valuesListType);
                            break;
                        case NUMBER:
                            pageNumber = context.readValue(parser, Integer.class);
                            break;
                        case SIZE:
                            pageSize = context.readValue(parser, Integer.class);
                            break;
                        case TOTAL_ELEMENTS:
                            total = context.readValue(parser, Long.class);
                            break;
                        default:
                            parser.skipChildren();
                            break;
                    }
                } while (((propName = parser.nextFieldName())) != null);
            } else {
                context.handleUnexpectedToken(handledType(), parser);
            }
        } else {
            context.handleUnexpectedToken(handledType(), parser);
        }

        //Note that Sort field of Page is ignored here.
        //Feel free to add more switch cases above to deserialize it as well.
        return new PageImpl<>(list, PageRequest.of(pageNumber, pageSize), total);
    }

    /**
     * This is the main point here.
     * The PageDeserializer is created for each specific deserialization with concrete generic parameter type of Page.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) {
        //This is the Page actually
        final JavaType wrapperType = context.getContextualType();
        final PageDeserializer deserializer = new PageDeserializer();
        //This is the parameter of Page
        deserializer.valueType = wrapperType.containedType(0);
        return deserializer;
    }
}