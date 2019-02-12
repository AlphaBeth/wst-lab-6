package ru.ifmo.wst.lab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@Data
@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
public class ExterminatusInfo {
    private String initiator;
    private String reason;
    private String method;
    private String planet;
    private Date date;
}
