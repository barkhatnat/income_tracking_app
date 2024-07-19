package ru.barkhatnat.income_tracking.utils;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.barkhatnat.income_tracking.DTO.AccountDto;
import ru.barkhatnat.income_tracking.DTO.AccountResponseDto;
import ru.barkhatnat.income_tracking.entity.Account;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    AccountDto toAccountDto(Account account);
    AccountResponseDto toAccountResponseDto(Account account);

    Iterable<AccountDto> toAccountDtoCollection(Iterable<Account> accounts);

    Iterable<AccountResponseDto> toAccountResponseDtoCollection(Iterable<Account> accounts);
}
