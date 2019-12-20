package org.haobtc.wallet.bean;

import java.util.List;

public class GetnewcreatTrsactionListBean {


    /**
     * txid : 7e7d38c1a75c9e9322cb7cb5900270d480e860bfca607509ad8b78294170a300
     * can_broadcast : false
     * amount : 0
     * fee : 10000
     * description :
     * tx_status : Unsigned
     * sign_status : [0,2]
     * output_addr : [{"addr":"tb1qd6d098z55znz65wmk7rqmcgfxy6vtxc602kny400x6pal232x9ys3ed9ry","amount":100000},{"addr":"tb1qng4x9xgz4ffxhgp38jh5adp7ssu6jd7jkplq5zh3wdw66mzadr7sycfssq","amount":890000}]
     * input_addr : ["tb1q48kq864pz6g3tr2zzj4ugufhy4nrncuaz9w2v4c57n62ascramhqgfmknr"]
     * cosigner : ["Vpub5gyCX33B53xAyfEaH1Jfnp5grizbHfxVz6bWLPD92nLcbKMsQzSbM2eyGiK4qiRziuoRhoeVMoPLvEdfbQxGp88PN9cU6zupSSuiPi3RjEg","Vpub5gLTnhnQig7SLNhWCqE2AHqt8zhJGQwuwEAKQE67bndddSzUMAmab7DxZF9b9wynVyY2URM61SWY67QYaPV6oQrB41vMKQbeHveRvuThAmm"]
     */

    private String txid;
    private boolean can_broadcast;
    private int amount;
    private int fee;
    private String description;
    private String tx_status;
    private List<Integer> sign_status;
    private List<OutputAddrBean> output_addr;
    private List<String> input_addr;
    private List<String> cosigner;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public boolean isCan_broadcast() {
        return can_broadcast;
    }

    public void setCan_broadcast(boolean can_broadcast) {
        this.can_broadcast = can_broadcast;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTx_status() {
        return tx_status;
    }

    public void setTx_status(String tx_status) {
        this.tx_status = tx_status;
    }

    public List<Integer> getSign_status() {
        return sign_status;
    }

    public void setSign_status(List<Integer> sign_status) {
        this.sign_status = sign_status;
    }

    public List<OutputAddrBean> getOutput_addr() {
        return output_addr;
    }

    public void setOutput_addr(List<OutputAddrBean> output_addr) {
        this.output_addr = output_addr;
    }

    public List<String> getInput_addr() {
        return input_addr;
    }

    public void setInput_addr(List<String> input_addr) {
        this.input_addr = input_addr;
    }

    public List<String> getCosigner() {
        return cosigner;
    }

    public void setCosigner(List<String> cosigner) {
        this.cosigner = cosigner;
    }

    public static class OutputAddrBean {
        /**
         * addr : tb1qd6d098z55znz65wmk7rqmcgfxy6vtxc602kny400x6pal232x9ys3ed9ry
         * amount : 100000
         */

        private String addr;
        private int amount;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
