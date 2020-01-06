package com.thoughtworks.payment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;

public class PaymentDeSerializer extends StdDeserializer<Payment> {
    protected PaymentDeSerializer(Class<Payment> t) {
        super(t);
    }

    public PaymentDeSerializer() {
        this(null);
    }

    @Override
    public Payment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        int amount = (Integer) ((IntNode) node.get("amount")).numberValue();
        String beneficiaryName = node.get("beneficiary").get("name").asText();
        long beneficiaryAccountNumber = node.get("beneficiary").get("accountNumber").asLong();
        String beneficiaryIfscCode = node.get("beneficiary").get("ifscCode").asText();
        BankDetails beneficiary = new BankDetails(beneficiaryName, beneficiaryAccountNumber, beneficiaryIfscCode);

        String payeeName = node.get("payee").get("name").asText();
        long payeeAccountNumber = node.get("payee").get("accountNumber").asLong();
        String payeeIfscCode = node.get("payee").get("ifscCode").asText();
        BankDetails payee = new BankDetails(payeeName, payeeAccountNumber, payeeIfscCode);

        return new Payment(amount, beneficiary, payee);
    }
}
