package com.lucascoruqieri.mc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.lucascoruqieri.mc.domain.Cliente;
import com.lucascoruqieri.mc.domain.enums.TipoCliente;
import com.lucascoruqieri.mc.dto.ParceiroNegocioDTO;
import com.lucascoruqieri.mc.repositories.ClienteRepository;
import com.lucascoruqieri.mc.resources.exception.FieldMessage;
import com.lucascoruqieri.mc.services.validation.utils.ValidacaoParceiroNegocio;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ParceiroNegocioDTO> {

	@Autowired
	private ClienteRepository repo;

	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ParceiroNegocioDTO objDto, ConstraintValidatorContext context) {

		List<FieldMessage> list = new ArrayList<>();

		if (objDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod())
				&& !ValidacaoParceiroNegocio.isValidCPF(objDto.getCpfcnpj())) {
			list.add(new FieldMessage("cpfcnpj", "CPF inválido"));
		}

		if (objDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod())
				&& !ValidacaoParceiroNegocio.isValidCNPJ(objDto.getCpfcnpj())) {
			list.add(new FieldMessage("cpfcnpj", "CNPJ inválido"));
		}

		Cliente aux = repo.findByEmail(objDto.getEmail());
		if (aux != null) {
			list.add(new FieldMessage("email", "Email já existente"));
		}

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
