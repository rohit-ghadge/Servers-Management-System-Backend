package io.getarrays.server.service.implementation;

import io.getarrays.server.model.Server;
import io.getarrays.server.repo.ServerRepo;
import io.getarrays.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Random;

import static io.getarrays.server.enumeration.Status.SERVER_DOWN;
import static io.getarrays.server.enumeration.Status.SERVER_UP;
import static java.lang.Boolean.TRUE;
import static org.springframework.data.domain.PageRequest.of;

/**
 * @author Rohit Ghadge
 * @version 1.0
 * @Date 8 Feb 2022
 */

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImpl implements ServerService 
{
    private final ServerRepo serverRepo;

    @Override
    public Server create(Server server) 
    {
        log.info("Saving new server: {}", server.getName());
        // Set image for Server
        server.setImageUrl(setServerImageUrl());
        // Save Server inside database
        return serverRepo.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException 
    {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        System.out.println("******************************"+isReachable(ipAddress,80,10000)+"************************");
        server.setStatus(isReachable(ipAddress,80,10000) ? SERVER_UP : SERVER_DOWN);
        serverRepo.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) 
    {
        log.info("Fetching all servers");
        // return server list limit
        return serverRepo.findAll(of(0, limit)).toList();
    }

    @Override
    public Server get(Long id) 
    {
        log.info("Fetching server by id: {}", id);
        return serverRepo.findById(id).get();
    }

    @Override
    public Server update(Server server)
    {
        log.info("Updating server: {}", server.getName());
        return serverRepo.save(server);
    }

    @Override
    public Boolean delete(Long id) 
    {
        log.info("Deleting server by ID: {}", id);
        serverRepo.deleteById(id);
        return TRUE;
    }

    private String setServerImageUrl() 
    {
        String[] imageNames = { "server1.png", "server2.png", "server3.png", "server4.png" };
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image/" + imageNames[new Random().nextInt(4)]).toUriString();
    }

    private boolean isReachable(String ipAddress, int port, int timeOut) 
    {
        try 
        {
            try(Socket socket = new Socket()) 
            {
                socket.connect(new InetSocketAddress(ipAddress, port), timeOut);
            }
            return true;
        }
        catch (IOException exception)
        {
            return false;
        }
    }
}
