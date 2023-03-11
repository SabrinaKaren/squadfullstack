package br.com.elogroup.squadfullstack.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.elogroup.squadfullstack.api.exception.BadRequestException;
import br.com.elogroup.squadfullstack.api.model.DoubtToCreate;
import br.com.elogroup.squadfullstack.api.model.DoubtToListAndUpdate;
import br.com.elogroup.squadfullstack.domain.model.Doubt;
import br.com.elogroup.squadfullstack.domain.sevice.DoubtService;
import br.com.elogroup.squadfullstack.util.MessageUtil;
import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/doubt")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class DoubtController extends BaseController {

	private final DoubtService doubtService;
	private final ModelMapper modelMapper;
	private final LocalValidatorFactoryBean beanValidator;
	private final MessageUtil msgUtil;

	@GetMapping()
	@ResponseBody
	public ResponseWrapper<DoubtToListAndUpdate> getAll() {
		ResponseWrapper<DoubtToListAndUpdate> responseWrapper = new ResponseWrapper<DoubtToListAndUpdate>();
		responseWrapper.setData(new ArrayList<DoubtToListAndUpdate>());

		List<Doubt> users = doubtService.getDoubts();
		responseWrapper.setData(new ArrayList<DoubtToListAndUpdate>());
		assemblyResponseSuccessOperation(responseWrapper);

		if (users != null && !users.isEmpty()) {
			List<DoubtToListAndUpdate> usersDto = users.stream().map(this::toDoubtDTO).collect(Collectors.toList());

			responseWrapper.setData(usersDto);

		} else {
			responseWrapper.setMessage(msgUtil.getLocalizedMessage("operation.doubt.findAll.notFound"));
			responseWrapper.setSuccess(false);
		}

		return responseWrapper;
	}

	@GetMapping("/{id}")
	@ResponseBody
	public ResponseWrapper<DoubtToListAndUpdate> getById(@PathVariable Long id) {
		ResponseWrapper<DoubtToListAndUpdate> responseWrapper = new ResponseWrapper<DoubtToListAndUpdate>();
		responseWrapper.setData(new ArrayList<DoubtToListAndUpdate>());

		Doubt doubt = doubtService.geDoubtById(id);
		responseWrapper.setData(new ArrayList<DoubtToListAndUpdate>());
		assemblyResponseSuccessOperation(responseWrapper);

		if (doubt != null) {
			responseWrapper.setData(Collections.singletonList(modelMapper.map(doubt, DoubtToListAndUpdate.class)));
		} else {
			responseWrapper.setMessage(msgUtil.getLocalizedMessage("operation.doubt.findById.notFound"));
			responseWrapper.setSuccess(false);
		}

		return responseWrapper;
	}

	@PostMapping()
	@ResponseBody
	public ResponseWrapper<DoubtToCreate> create(@RequestBody DoubtToCreate doubtInput) throws Exception {
		ResponseWrapper<DoubtToCreate> responseWrapper = new ResponseWrapper<DoubtToCreate>();
		responseWrapper.setData(new ArrayList<DoubtToCreate>());

		Set<ConstraintViolation<DoubtToCreate>> validate = beanValidator.validate(doubtInput);

		if (!validate.isEmpty()) {
			throw new BadRequestException(
					validate.stream().map(mess -> String.format("'%s'", mess.getMessage().toString()))
							.collect(Collectors.toList()).toString());
		} else {

			Doubt user = modelMapper.map(doubtInput, Doubt.class);
			DoubtToListAndUpdate userDto = toDoubtDTO(doubtService.create(user));

			assemblyResponseSuccessOperation(responseWrapper);
			responseWrapper.setData(Arrays.asList(userDto));
			responseWrapper
					.setMessage(msgUtil.getLocalizedMessage("operation.doubt.create.success", user.getQuestion()));
		}

		return responseWrapper;
	}

	@PutMapping
	@ResponseBody
	public ResponseWrapper<DoubtToListAndUpdate> change(@RequestBody DoubtToListAndUpdate doubtInput) throws Exception {
		ResponseWrapper<DoubtToListAndUpdate> responseWrapper = new ResponseWrapper<DoubtToListAndUpdate>();
		responseWrapper.setData(new ArrayList<DoubtToListAndUpdate>());

		Set<ConstraintViolation<DoubtToCreate>> validate = beanValidator.validate(doubtInput);
		if (!validate.isEmpty()) {
			throw new BadRequestException(
					validate.stream().map(mess -> String.format("'%s'", mess.getMessage().toString()))
							.collect(Collectors.toList()).toString());
		} else {
			Doubt user = modelMapper.map(doubtInput, Doubt.class);
			DoubtToListAndUpdate userDto = toDoubtDTO(doubtService.change(user));

			assemblyResponseSuccessOperation(responseWrapper);
			responseWrapper.setData(Arrays.asList(userDto));
			responseWrapper
					.setMessage(msgUtil.getLocalizedMessage("operation.doubt.change.success", user.getQuestion()));
		}

		return responseWrapper;
	}

	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseWrapper<DoubtToListAndUpdate> delete(@PathVariable Long id) throws Exception {
		ResponseWrapper<DoubtToListAndUpdate> responseWrapper = new ResponseWrapper<DoubtToListAndUpdate>();
		responseWrapper.setData(new ArrayList<DoubtToListAndUpdate>());

		Doubt user = doubtService.delete(id);

		assemblyResponseSuccessOperation(responseWrapper);
		responseWrapper.setMessage(msgUtil.getLocalizedMessage("operation.doubt.delete.success", user.getQuestion()));

		return responseWrapper;
	}

	private DoubtToListAndUpdate toDoubtDTO(Doubt user) {
		return modelMapper.map(user, DoubtToListAndUpdate.class);
	}

}
