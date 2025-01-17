package telran.java52.person.service;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java52.person.dao.PersonRepository;
import telran.java52.person.dto.AddressDto;
import telran.java52.person.dto.CityPopulationDto;
import telran.java52.person.dto.PersonDto;
import telran.java52.person.dto.exceptions.PersonNotFoundException;
import telran.java52.person.model.Address;
import telran.java52.person.model.Child;
import telran.java52.person.model.Employee;
import telran.java52.person.model.Person;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService, CommandLineRunner {
	final PersonRepository personRepository;
	final ModelMapper modelMapper;

	@Transactional
	@Override
	public Boolean addPerson(PersonDto personDto) {
		if (personRepository.existsById(personDto.getId())) {
			return false;
		}
		personRepository.save(modelMapper.map(personDto, Person.class));
		return true;
	}

	@Override
	public PersonDto findPersonById(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		return modelMapper.map(person, PersonDto.class);
	}

	@Transactional
	@Override
	public PersonDto removePerson(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
		personRepository.delete(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Transactional
	@Override
	public PersonDto updatePersonName(Integer id, String name) {
		Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
		person.setName(name);
		return modelMapper.map(person, PersonDto.class);
	}

	@Transactional
	@Override
	public PersonDto updatePersonAddress(Integer id, AddressDto addressDto) {
		Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
		person.setAddress(modelMapper.map(addressDto, Address.class));
		return modelMapper.map(person, PersonDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public PersonDto[] findPersonsByCity(String city) {
		return personRepository.findByAddressCityIgnoreCase(city)
				.map(p -> modelMapper.map(p, PersonDto.class))
				.toArray(PersonDto[]::new);
	}

	@Transactional(readOnly = true)
	@Override
	public PersonDto[] findPersonsByName(String name) {
		return personRepository.findByNameIgnoreCase(name)
				.map(p -> modelMapper.map(p, PersonDto.class))
				.toArray(PersonDto[]::new);
	}

	@Transactional(readOnly = true)
	@Override
	public PersonDto[] findPersonsBetweenAge(Integer minAge, Integer maxAge) {
		LocalDate from = LocalDate.now().minusYears(maxAge);
		LocalDate to = LocalDate.now().minusYears(minAge);
		return personRepository.findByBirthDateBetween(from, to)
				.map(p -> modelMapper.map(p, PersonDto.class))
				.toArray(PersonDto[]::new);
	}

	@Override
	public Iterable<CityPopulationDto> getCitiesPopulation() {
		return personRepository.getCitiesPopulation();
	}

	@Transactional
	@Override
	public void run(String... args) throws Exception {
		if(personRepository.count() == 0) {
			Person person = new Person(1000, "John", LocalDate.of(1985, 3, 11), 
					new Address("Tel Aviv", "Ben Gvirol", 81));
			Child child = new Child(2000, "Mosche", LocalDate.of(2018, 7, 5),
					new Address("Ashkelon", "Bar Kohva", 21), "Shalom");
			Employee employee = new Employee(3000, "Sarah", LocalDate.of(1995, 11, 23), 
					new Address("Rehovot", "Herzl", 7), "Motorola", 20_000);
			personRepository.save(person);
			personRepository.save(child);
			personRepository.save(employee);
		}
		
	}

}
