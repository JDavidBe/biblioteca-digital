package com.biblioteca.auth.infraestructure.driver_adapters.jpa_repository;
import com.biblioteca.auth.domain.model.Credencial;
import com.biblioteca.auth.domain.model.gateway.CredencialGateway;
import com.biblioteca.auth.infraestructure.mapper.CredencialMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
@Repository
@RequiredArgsConstructor
public class CredencialGatewayImpl implements CredencialGateway {
    private final CredencialJpaRepository jpaRepository;
    private final CredencialMapper mapper;
    @Override
    public Credencial buscarPorCorreo(String correo) {
        return jpaRepository.findByCorreo(correo).map(mapper::toDomain).orElse(null);
    }
    @Override
    public Credencial guardar(Credencial credencial) {
        CredencialData data = mapper.toData(credencial);
        return mapper.toDomain(jpaRepository.save(data));
    }
    @Override
    public Boolean existePorCorreo(String correo) {
        return jpaRepository.existsByCorreo(correo);
    }
    @Override
    public void eliminarPorCorreo(String correo) {
        jpaRepository.findByCorreo(correo).ifPresent(jpaRepository::delete);
    }
}
