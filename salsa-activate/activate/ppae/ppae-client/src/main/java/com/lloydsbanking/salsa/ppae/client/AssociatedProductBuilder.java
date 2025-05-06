package com.lloydsbanking.salsa.ppae.client;

import lib_sim_bo.businessobjects.*;

import java.util.List;

public class AssociatedProductBuilder {

    Product associatedProduct;

    public AssociatedProductBuilder() {
        this.associatedProduct = new Product();
    }

    public Product build() {
        return associatedProduct;
    }

    public AssociatedProductBuilder productIdentifier(String productIdentifier) {
        associatedProduct.setProductIdentifier(productIdentifier);
        return this;
    }

    public AssociatedProductBuilder externalSystemProductIdentifier(List<ExtSysProdIdentifier> externalSystemProductIdentifier) {
        associatedProduct.getExternalSystemProductIdentifier().addAll(externalSystemProductIdentifier);
        return this;
    }

    public AssociatedProductBuilder productOptions(List<ProductOptions> productOptions) {
        associatedProduct.getProductoptions().addAll(productOptions);
        return this;
    }

    public AssociatedProductBuilder productName(String productName) {
        associatedProduct.setProductName(productName);
        return this;
    }

    public AssociatedProductBuilder instructionDetails(InstructionDetails instructionDetails) {
        associatedProduct.setInstructionDetails(instructionDetails);
        return this;
    }

    public AssociatedProductBuilder guaranteedOfferCode(String guaranteedOfferCode) {
        associatedProduct.setGuaranteedOfferCode(guaranteedOfferCode);
        return this;
    }

    public AssociatedProductBuilder productOffer(List<ProductOffer> productOffer) {
        associatedProduct.getProductoffer().addAll(productOffer);
        return this;
    }

    public AssociatedProductBuilder productPropositionIdentifier(String productPropositionIdentifier) {
        associatedProduct.setProductPropositionIdentifier(productPropositionIdentifier);
        return this;
    }

}
