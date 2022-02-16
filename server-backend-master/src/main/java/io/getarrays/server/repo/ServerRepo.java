package io.getarrays.server.repo;

import io.getarrays.server.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Rohit Ghadge
 * @version 1.0
 * @Date 8 Feb 2022
 */

public interface ServerRepo extends JpaRepository<Server, Long> 
{
    Server findByIpAddress(String ipAddress);
}
