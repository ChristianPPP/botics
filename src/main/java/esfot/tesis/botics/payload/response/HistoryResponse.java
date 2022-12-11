package esfot.tesis.botics.payload.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryResponse {
    private long id;
    private boolean state;
    private String changeDetails;
    private String labName;
    private String hostName;
    private String codeCpu;
}
