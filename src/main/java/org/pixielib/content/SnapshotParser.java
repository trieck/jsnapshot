package org.pixielib.content;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class SnapshotParser {

    private static final ObjectMapper mapper = new ObjectMapper();
    private final List<Phrase> phrases;

    SnapshotParser() {
        phrases = new ArrayList<>();
    }

    public void writePhrases(Event event) {
        ArrayNode aPhrases = mapper.createArrayNode();

        for (Phrase phrase : phrases ) {
            ObjectNode node = mapper.createObjectNode();
            node.put("text", phrase.text);
            node.put("status", Phrase.toString(phrase.status));
            aPhrases.add(node);
        }

        event.setPhrases(aPhrases);
    }

    void addPhrase(String text, Phrase.PhraseStatus status) {
        text = text.replaceAll("\\xA0", "").trim();
        phrases.add(new Phrase(text, status));
    }

    public void parse(EventBuffer buffer) {

        String text = buffer.getMeta("Text");
        String value = buffer.getMeta("Value");
        String targetType = buffer.getMeta("NormalTargetType");

        if (StringUtils.isNotEmpty(text) &&
                (StringUtils.equalsIgnoreCase(targetType, "Form") ||
                        StringUtils.equalsIgnoreCase(targetType, "Mdi Child"))) {
            addPhrase(text, Phrase.PhraseStatus.Title);
        } else if (StringUtils.isNotEmpty(text)) {
            addPhrase(text, Phrase.PhraseStatus.Regular);
        }

        if (StringUtils.isNotEmpty(value) && !StringUtils.equals(text, value)) {
            // TODO: parse xml
            addPhrase(value, Phrase.PhraseStatus.Regular);
        }
    }
}
