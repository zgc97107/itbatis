package org.example.bean;

import com.itbatis.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zgc
 * @since 2020/7/1
 */
@Data
@Accessors(chain = true)
public class User {
    @TableId("id")
    private Long id;
    private String username;
    private String password;
    private String phoneNumber;
}
