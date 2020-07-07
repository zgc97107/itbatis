package org.example.bean;

import com.itbatis.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zgc
 * @since 2020/7/6
 */
@Data
@Accessors(chain = true)
public class Student {
    @TableId
    private Integer id;
    private String cardNum;
    private String name;
}
